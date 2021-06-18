package com.devopskindergarten.xtermssh.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import com.devopskindergarten.xtermssh.domain.SshDto;
import com.devopskindergarten.xtermssh.domain.SshConnectionDto;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class SshConnectionService {

    private static Map<WebSocketSession, Object> sshMap = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void initConnection(WebSocketSession session, SshDto dto) {
        // 초기 설정. jsch 클래스를 생성해서 객체를 connectionDto에 넣고,
        // 웹소켓도 넣은 자료구조로 만들어 connectToSSH로 보냄.
        JSch jsch = new JSch();
        SshConnectionDto connectionDto = new SshConnectionDto();
        // 생성한 객체에 데이터들을 넣어주는 단계.
        connectionDto.setJsch(jsch);
        connectionDto.setSession(session);
        connectionDto.setInfo(dto);
        sshMap.put(session, connectionDto);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    connectToSSH(connectionDto, dto, session);
                } catch (JSchException | IOException e) {
//                    log.error("에러 정보: {}", e);
                    close(session);
                }
            }
        });
    }

    public void recvHandle(WebSocketSession session, String command) {
        SshConnectionDto connectionDto = (SshConnectionDto) sshMap.get(session);

        if (connectionDto != null) {
            try {
                transToSSh(connectionDto.getChannel(), command);
            } catch (IOException e) {
//                log.error("에러 정보: {}", e);
                close(session);
            }
        }
    }

    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    public void close(WebSocketSession session) {
        SshConnectionDto connectionDto = (SshConnectionDto) sshMap.get(session);
        if (connectionDto != null) {
            if (connectionDto.getChannel() != null) connectionDto.getChannel().disconnect();
            sshMap.remove(session);
        }
    }

    private void connectToSSH(SshConnectionDto connectionDto, SshDto dto, WebSocketSession webSocketSession) throws JSchException, IOException {
        Session session = null;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        // init에서 받아온 connectionDto(데이터 뭉치)에서 jsch 객체를 꺼내,
        // 그 때 세션에 들어 있던 접속 아이디, 주소, 포트를 현재 세션에 연결시킴.
        session = connectionDto.getJsch().getSession(dto.getUsername(), dto.getHost(), dto.getPort());
        session.setConfig(config);

        // 연결하려는 곳의 비밀번호를 입력.
        session.setPassword(dto.getPassword());
        session.connect(60000);

        // shell을 매개변수로 해서 채널을 오픈해 생성.
        Channel channel = session.openChannel("shell");

        // 채널 연결
        channel.connect(3000);
        connectionDto.setChannel(channel);

        // ssh 연결 후 값을 입력
        InputStream is = channel.getInputStream();
        try {
            byte[] buffer = new byte[1024];
            int i = 0;
            while((i = is.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }
        } finally {
            session.disconnect();
            channel.disconnect();
            if (is != null) {
                is.close();
            }
        }
    }

    private void transToSSh(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream os = channel.getOutputStream();
            if (command.equals("SIGINT")) {
                os.write(3);
            } else if(command.equals("SIGTSTP")) {
                os.write(26);
            } else {
                os.write(command.getBytes());
            }
            os.flush();
        }
    }

}
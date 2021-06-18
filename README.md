# devops-kindergarten-web-term-be
스프링을 이용한 컨테이너 - ssh 중개 서버

---

### 통신 dependency
-spring-web-socket : 스프링 웹소켓
-jsch : ssh 통신 (sftp)

---
### 참고 자료
<a>https://velog.io/@hanblueblue/%EB%B2%88%EC%97%AD-Spring-4-Spring-WebSocket
<a>https://mebadong.tistory.com/40
<a>https://supawer0728.github.io/2018/03/30/spring-websocket/
<a>https://lts0606.tistory.com/6

---
### 클래스
SshHandler : web socket handler 인터페이스 구현 (웹소켓 여러 세션 정보 담게 map으로 구현하는 것도 괜찮아 보임)
SshConnectionService : 소켓이 연결되면 JSch를 생성하고 shell 채널로 연결. 소켓에서 메세지 주고받기 처리함.
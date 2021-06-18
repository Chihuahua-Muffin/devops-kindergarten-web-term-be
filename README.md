# devops-kindergarten-web-term-be
스프링을 이용한 컨테이너 - ssh 중개 서버

---

### 통신 dependency
-spring-web-socket : 스프링 웹소켓  
-jsch : ssh 통신

---
### 참고 자료
📎 : 중요
#### Spring-WebSocket
<a>https://velog.io/@hanblueblue/%EB%B2%88%EC%97%AD-Spring-4-Spring-WebSocket  
#### 📎Java Spring을 이용한 Web Terminal 구현하기
<a>https://mebadong.tistory.com/40  
#### Spring WebSocket 소개
<a>https://supawer0728.github.io/2018/03/30/spring-websocket/  
#### 📎jsch 사용법
<a>https://lts0606.tistory.com/6  
#### 📎스프링부트에서 채팅프로그램 만들기
<a>https://myhappyman.tistory.com/100

---
### 클래스
SshHandler : web socket handler 인터페이스 구현 (웹소켓 여러 세션 정보 담게 map으로 구현하는 것도 괜찮아 보임)
SshConnectionService : 소켓이 연결되면 JSch를 생성하고 shell 채널로 연결. 소켓에서 메세지 주고받기 처리함.

---

## 대부분의 코드는
<a>https://mebadong.tistory.com/40
에서 받아왔습니다.
 
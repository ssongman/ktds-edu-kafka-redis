# < 시작전에 >





# 1. 실습 환경 준비

우리는 Kubernetes 기반에 Kafka / Redis 설치하는 실습을 진행할 것이다.

Cloud 환경에 Kubernetes가 설치된 VM 이 개인별 하나씩 준비되어 있어 있다.

그러므로 개인 PC에서 VM 접속할 수 있는 Terminal 을 설치해야 한다.



## 1.1 MobaxTerm 설치

Cloud VM에 접근하기 위해서는 터미널이 필요하다.

CMD / PowerShell / putty 와 같은 기본 터미널을 이용해도 되지만 좀더 많은 기능이 제공되는 MobaxTerm(free 버젼) 을 다운로드 하자.



- download 위치
  - 링크: https://download.mobatek.net/2312023031823706/MobaXterm_Installer_v23.1.zip

- mobaxterm 실행

![image-20220702160114315](beforebegin.assets/image-20220702160114315.png)







## 1.2 gitBash 설치

교육문서를 다운로드 받으려면 Git Command 가 필요하다. Windows 에서는 기본 제공되지 않아 별도 설치 해야 한다.

- 다운로드 주소 : https://github.com/git-for-windows/git/releases/download/v2.40.1.windows.1/Git-2.40.1-64-bit.exe
- 참조 링크 : https://git-scm.com/





## 1.3 Typora 설치

### (1) 설치

- 링크 : https://typora.io/windows/typora-setup-x64.exe?0611



### (2) typora 환경설정

원할한 실습을 위해 코드펜스 옵션을 아래와 같이 변경하자.

- 코드펜스 설정
  - 메뉴 : 파일 > 환경설정 > 마크다운 > 코드펜스
    - 코드펜스에서 줄번호 보이기 - check
    - 긴문장 자동 줄바꿈 : uncheck




![image-20220702154444773](beforebegin.assets/image-20220702154444773.png)



- 개요보기 설정
  - 메뉴 : 보기 > 개요
    - 개요 : check





## 1.4 STS 설치

### (1) STS 설치

- 링크 : https://download.springsource.com/release/STS4/4.18.1.RELEASE/dist/e4.27/spring-tool-suite-4-4.18.1.RELEASE-e4.27.0-win32.win32.x86_64.self-extracting.jar
- 설치
- Workspace 설정
  - 위치 : C:\workspace_STS4.18.1




### (2) [참고] java 설치

- java 설치가 필요한 경우 아래 링크 참고
  - 링크: https://javadl.oracle.com/webapps/download/AutoDL?BundleId=246442_2dee051a5d0647d5be72a7c0abff270e






# 2. 교육문서 Download

해당 교육문서는 모두 markdown 형식으로 작성되었다.  Chrome Browser 에서 github 문서를 직접 확인해도 된다.

하지만 실습을 따라가다 보면 개인별로 수정해야 할 부분이 있는데 web browser 에서는 수정이 안되기 때문에 수정이 용이한 환경이 훨씬 좋을 것이다.

좀더 효율적인 실습을 위해서 해당 자료를 download 하여 markdown 전용 viewer 인 Typora 로 오픈하여 실습에 참여하자.





## 2.1 교육문서 Download

gitbash 실행후 command 명령어로 아래와 같이 임의의 디렉토리를 생성후 git clone 으로 download 하자.

```sh
# GitBash 실행

# 본인 PC에서 아래 디렉토리를 생성
$ mkdir -p /c/githubrepo
 
 
$ cd /c/githubrepo

$ git clone https://github.com/ssongman/ktds-edu-kafka-redis.git
Cloning into 'ktds-edu2'...
remote: Enumerating objects: 424, done.
remote: Counting objects: 100% (424/424), done.
remote: Compressing objects: 100% (305/305), done.
remote: Total 424 (delta 123), reused 397 (delta 96), pack-reused 0 eceiving objects:  88% (374/424), 11.09 MiB | 5.54 MiB/s
Receiving objects: 100% (424/424), 12.94 MiB | 5.67 MiB/s, done.
Resolving deltas: 100% (123/123), done.


$ ll /c/githubrepo
drwxr-xr-x 1 ssong 197609 0 Jun 11 14:27 ktds-edu-kafka-redis/

```



## 2.2 Typora 로 readme.md 파일오픈



- typora 로 오픈

```
## typora 에서 아래 파일 오픈

C:\githubrepo\ktds-edu-kafka-redis\README.md
```

![image-20220702160433029](beforebegin.assets/image-20220702160433029.png)





# 3. 실습 환경 준비(Cloud)



## 3.1 수강생별 접속 서버 주소

개인별 VM Server 접속 환경 및 Kafka 실습을 위한 개인 Topic 정보를 확인하자.

| 담당자 | VM Server | VM  Server IP | kafka  Topic | kafka Group | 비고 |
| :----: | :-------: | :-----------: | :----------: | :---------: | :--: |
| 송양종 | bastion01 | 34.27.41.215  | edu-topic01  | edu-group01 |      |
| 송양종 | bastion02 |               | edu-topic02  | edu-group02 |      |
| 김상훈 | bastion03 | 35.247.230.92 | edu-topic03  | edu-group03 |      |
| 강현종 | bastion04 |               | edu-topic04  | edu-group04 |      |
| 김석천 | bastion05 |               | edu-topic05  | edu-group05 |      |
| 김용건 | bastion06 |               | edu-topic06  | edu-group06 |      |
| 석미화 | bastion07 |               | edu-topic07  | edu-group07 |      |
| 안성호 | bastion08 |               | edu-topic08  | edu-group08 |      |
| 양행모 | bastion09 |               | edu-topic09  | edu-group09 |      |
| 윤준구 | bastion10 |               | edu-topic10  | edu-group10 |      |
| 윤하림 | bastion11 |               | edu-topic11  | edu-group11 |      |
| 이동현 | bastion12 |               | edu-topic12  | edu-group12 |      |
| 이영호 | bastion13 |               | edu-topic13  | edu-group13 |      |
| 임성식 | bastion14 |               | edu-topic14  | edu-group14 |      |
| 조은하 | bastion15 |               | edu-topic15  | edu-group15 |      |
| 최소현 | bastion16 |               | edu-topic16  | edu-group16 |      |
|        | bastion17 |               | edu-topic17  | edu-group17 |      |
|        | bastion18 |               | edu-topic18  | edu-group18 |      |
|        | bastion19 |               | edu-topic19  | edu-group19 |      |
|        | bastion20 |               | edu-topic20  | edu-group20 |      |





## 3.2 ssh (Mobaxterm) 실행

Mobaxterm 을 실행하여 VM 접속정보를 위한 신규 sesion 을 생성하자.

- 메뉴
  - Session  : 상단 좌측아이콘 클릭

  - SSH : 팝업창 상단 아이콘 클릭

- Romote host
  - 개인별로 접근 주소가 다르므로 위 수강생별  VM  Server IP 주소를 확인하자.
  - ex)  bastion03 : 35.247.230.92

- User
  - Specify username 에 Check
  - User : ktdseduuser  입력

- Port : 22
- Advanced SSH settings
  - Use private key : C:\githubrepo\ktds-edu-kafka-redis\gcp-vm-key\ktdseduuser
    - 교육자료 Download 되는 자료에 위 key가 포함되어 있음





![image-20230514022214007](assets/image-20230514022214007.png)

빨간색 영역을 주의해서 입력한 후 접속하자.






## 3.3 실습자료 download

접속 완료 하였다면 테스트를 위해서 git clone 명령으로 실습 자료를 받아 놓자.

```sh

## githubrepo directory 생성
$ mkdir -p ~/githubrepo

$ cd ~/githubrepo

$ git clone https://github.com/ssongman/ktds-edu-kafka-redis.git
Cloning into 'ktds-edu-kafka-redis'...
remote: Enumerating objects: 320, done.
remote: Counting objects: 100% (320/320), done.
remote: Compressing objects: 100% (220/220), done.
remote: Total 320 (delta 95), reused 277 (delta 56), pack-reused 0
Receiving objects: 100% (320/320), 8.40 MiB | 24.22 MiB/s, done.
Resolving deltas: 100% (95/95), done.


# 확인
$ cd  ~/githubrepo/ktds-edu-kafka-redis

$ ll ~/githubrepo/ktds-edu-kafka-redis
total 44
drwxrwxr-x 8 ktdseduuser ktdseduuser 4096 Jun 11 05:53 ./
drwxrwxr-x 3 ktdseduuser ktdseduuser 4096 Jun 11 05:53 ../
drwxrwxr-x 8 ktdseduuser ktdseduuser 4096 Jun 11 05:53 .git/
-rw-rw-r-- 1 ktdseduuser ktdseduuser  382 Jun 11 05:53 .gitignore
-rw-rw-r-- 1 ktdseduuser ktdseduuser 4077 Jun 11 05:53 README.md
-rw-rw-r-- 1 ktdseduuser ktdseduuser  461 Jun 11 05:53 SUMMARY.md
drwxrwxr-x 3 ktdseduuser ktdseduuser 4096 Jun 11 05:53 beforebegin/
drwxrwxr-x 2 ktdseduuser ktdseduuser 4096 Jun 11 05:53 gcp-vm-key/
drwxrwxr-x 7 ktdseduuser ktdseduuser 4096 Jun 11 05:53 kafka/
drwxrwxr-x 3 ktdseduuser ktdseduuser 4096 Jun 11 05:53 ktcloud-setup/
drwxrwxr-x 6 ktdseduuser ktdseduuser 4096 Jun 11 05:53 redis/

```




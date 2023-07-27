



# Redis Persistence



# 1. 개요

persistence 는 Disk 와 같은 storage 에 데이터를 write 하는 것을 의미한다.

다음과 같은 옵션이 있다.



* RDB(Redis DB)
  * 지정된 간격으로 데이터 세트의 특정 시점 스냅샷을 저장한다.
* AOF(Append Only File)
  * 서버에서 수신한 모든 쓰기작업을 기록한다.
  * 서버 시작시 다시 재생되어 원본 데이터 세트를 재구성할 수 있다.
* 지속성없음
  * Persistence 비활성화
* RDB + AOF
  * 두개를결합할 수도 있다.



# 2. RDB 와 AOF 장단점

## 1) RDB

### 장점

* RDB는 매우 작은 단일 파일 특정 시점 표현이다.
* 백업에 적합
  * ex) 최근 24시간동안 매시간 RDB 파일을 보관하고 30일 동안 매일 RDB 스냅샷을 저장할 수 있다.
  * 이를 통해 재해 발생시 다양한 버전의 세트를 쉽게 복원할 수 있음
* 단일 압축 파일이므로 재해 복구에 매우 유용
* AOF 에 비해 큰 데이터 세트로 더 빠르게 재시작 할 수 있다.



### 단점

* RDB 는 특정 시점의 스냅샷을 생성하는 방식이므로 
* 정전과 같은 사유로 작동을 멈춘 경우 데이터 손실 가능성을 최소화 해야 하는 경우 RDB 는좋지 않은 방식이다.



## 2) AOF

### 장점

* fsync 방식을 이용하며 fsync 는 백그라운드 스레드를 사용하여 수행됨
* AOF 로그는 추가전용이므로 정전시 검색이나 손상 문제가 없다.
* AOF 에는 이해하기 쉽고 구문 분석이 용이한 형식으로 모든 작업의 로그가 하나씩 포함되어 있다.
* 파일 내보내기가 쉽우며 복구가 쉽다.
  * ex)  Flushall 을 해서 데이터가 모두 사라지더라도
  * 서버를 중지하고 최신명령을 제거한 다음 redis 를 다시 시작하기만 하면 데이터를 세트를 복원시킬 수 있



### 단점

* 동일한 데이터 세트에 대한 동등한 RDB 파일 보다 크다.

* AOF 는 정확한 fsync 정책에 따라 RDB 보다 느릴 수 있다.

  



# 3. RDB Snapshotting

기본적으로 redis 는 Disk 에 dmp.rdb 라는 이름의 snapshot 을 저장한다.

아래와 같은 옵션을 지정할 수 있다.

```sh

# 매 60초에 한번씩
# 적어도 1000 개의 key 가 변경될때  Dataset 을 dump 한다.
$ redis-cli config set save 60 1000



# 완전히 disabled 하고 싶을때는 아래와 같이 설정
$ redis-cli config set save ""


```







# 4. AOF Setting

```sh

# 
$ redis-cli config set appendonly yes

$ redis-cli config set save ""



# 확인
env REDIS_AOF_ENABLED


```









# 5. 모두 설정하지 않을때

```sh
# 
$ redis-cli config set appendonly no

$ redis-cli config set save ""

```




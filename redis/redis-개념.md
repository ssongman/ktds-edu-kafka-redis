


# Redis 개념



# 1. Redis 개요



## 1.1 Redis 개요

- [Redis](https://redis.io/) (REmote DIctionary Server의 약자)는 데이터베이스, 캐시 또는 메시지 브로커로 자주 사용되는 **오픈 소스 인메모리 DB** 


- list, map, set, and sorted set 과 같은 고급 데이터 유형을 저장하고 조작할 수 있음
- Redis는 다양한 형식의 키를 허용하고 서버에서 직접 수행되므로 클라이언트의 작업 부하를 줄일 수 있음 

- 기본적으로 DB 전체를 메모리에 보유하며 Disk 는 지속성을 위해서만 사용됨 

- Redis는 인기 있는 데이터 스토리지 솔루션이며 **GitHub, Pinterest, Snapchat, Twitter, StackOverflow, Flickr 등과 같은 거대 기술 기업에서 사용됨**



### (1) Redis를 사용하는 이유

- **아주 빠름.** ANSI C로 작성되었으며 Linux, Mac OS X 및 Solaris와 같은 POSIX 시스템에서 실행됨
- Redis는 종종 가장 인기 있는 키/값 데이터베이스 및 컨테이너와 함께 사용되는 **가장 인기 있는 NoSQL 데이터베이스로 선정**됨
- 캐싱 솔루션은 클라우드 데이터베이스 백엔드에 대한 호출 수를 줄임
- 클라이언트 API 라이브러리를 통해 애플리케이션에서 액세스할 수 있음
- Redis는 인기 있는 모든 프로그래밍 언어에서 지원됨
- **오픈 소스이며 안정적임**



### (2) 실제 세계에서 Redis 사용

- Twitter는 Redis 클러스터 내의 모든 사용자에 대한 타임라인을 저장함
- Pinterest는 데이터가 수백 개의 인스턴스에 걸쳐 샤딩되는 Redis Cluster에 사용자 팔로어 그래프를 저장함
- Github은 Redis를 대기열로 사용함





## 1.2 Redis Cluster



### (1) Redis Cluster

- [Redis 클러스터](https://redis.io/topics/cluster-tutorial) 는 **DB를 분할하여 데이터베이스를 확장**하여 복원력을 향상시키도록 설계된 **Redis Instance 들의 집합임**

- **만약 Master에 연결할 수 없으면 해당 Slave가 Master로 승격됨** 
- 3개의 Master노드로 구성된 최소 Redis 클러스터에서 각 Master 노드에는 단일 Slave 노드가 있습니다(최소 장애 조치 허용)
- 각 Master노드에는 0에서 16,383 사이의 해시 슬롯 범위가 할당됨 
- 노드 A는 0에서 5000까지의 해시 슬롯, 노드 B는 5001에서 10000까지, 노드 C는 10001에서 16383까지 포함됨 

- **클러스터 내부 통신은 internal bus를 통해 이루어지며** 클러스터에 대한 정보를 전파하거나 **새로운 노드를 발견하기 위해 gossip protocol을 사용.** 

- 데이터는 여러 노드 간에 자동으로 분할되므로 노드의 하위 집합에 장애가 발생하거나 클러스터의 나머지 부분과 통신할 수 없는 경우에도 안정적인 서비스 제공





![kubernetes-deployment](redis-개념.assets/rancher_blog_deploying_kubernetes_1.png)







### (2) Redis 와 Redis-cluster 와의 차이점

| Redis                                                       | Redis Cluster                                                |
| ----------------------------------------------------------- | ------------------------------------------------------------ |
| 여러 데이터베이스 지원                                      | 한개의 데이터베이스 지원                                     |
| Single write point (single master)                          | Multiple write points (multiple masters)                     |
| ![redis-topology.png](redis-개념.assets/redis-topology.png) | ![redis-cluster-topology.png](redis-개념.assets/redis-cluster-topology.png) |





# 2. Caching 

Redis 를 사용하는 가장 큰 목적은 Caching이다.  Caching 이 무엇이고 주의사항등에 대해 알아보자.



## 2.1 정의

### (1) Caching 정의

- Caching 
  - 조회 비용이 많이 드는 데이터를 속도가 빠른 임시 공간에 저장해둠으로써 애플리케이션 처리속도를 높이는 방식임



### (2) Caching 프로세스

```
1. 데이터를 요청
2. 캐시에 있는지 확인
3. 캐시에 있다면 캐시에서 데이터를 가지고 오고 없다면 실제 저장공간에서 데이터를 Get
4. 실 저장공간에서 데이터를 가지고 왔다면 해당 데이터를 캐시에 저장함
```





## 2.2 Cache Hit



### (1) hit 관련 용어

- cache hit

  - 참조하려는 데이터가 캐시에 존재할 때 cache hit라 함

- cache miss 

  - 참조하려는 데이터가 캐시에 존재 하지 않을 때 cache miss라 함

- cache hit ratio(캐시 히트율)

  - ```
    (cache hit 횟수)/(전체참조횟수) = (cache hit 횟수)/(cache hit 횟수 + cache miss 횟수)
    ```



### (2) cache hit ratio 높이기 위한 방안

- 자주 참조되며 수정이 잘 발생하지 않는 데이터들로 구성되어야 한다.
- 데이터의 수정이 잦은 경우 데이터베이스 접근 및 캐시  데이터 일관성 처리 과정이 필요함.





## 2.3 Cache 사용시 주의사항

### (1) 대상

- 캐시의 대상은 모든 조회 데이터가 가능
- **cache miss가 발생하는 경우 실제 저장공간에서 데이터를 가져와야하기 때문에 비효율적**이라고 할 수 있음
- 캐싱의 활용도를 높이기 위해서는 **캐시 히트율을 높이는것이 중요함**



### (2) Local Caching

- Local Cache는 로컬 서버 내부 저장소에 데이터를 보관
- 서버에서 바로 데이터를 서비스할 수 있기 때문에 속도가  빠르다는 장점이 있음
- 하지만 다중 서버 환경에서는 각 서버에 중복된 데이터를 보관해야 하며 서버간 데이터 일관성이 깨질 수 있음
- 캐싱할 데이터의 양이 많을 경우 로컬서버의 메모리를 사용량이 많아져 결국 로컬서버의 부하를 초래할수 있어 신중하게 사용해야 함





# 3. Cache Pattern

일반적으로 데이터 공유의 목적으로 활용되며, 데이터유형, 사용 목적 등에 따라 다양한 적용 패턴이 있다.



## 3.1 Pattern 1 : 코드성 데이터 캐싱



### (1) 패턴상세

- 적용사례: 공통 코드 조회

- 예시 표준 흐름도

<img src="redis-개념.assets/clip_image002.png" alt="img" style="zoom: 150%;" />



 ### (2) 장/단점

- 장점
  - 하나의 통합 게이트웨이에서 공통/비지니스 업무를 개발 관리할 수 있음
  - 자주 사용되는 데이터만 cache하여 메모리 량을 최소화 할 수 있음
  - 메모리 데이터 expired 시간을 각 업무 마다 달리 할 수 있음
  - 시스템 장애 관리의 최소화 할 수 있음
- 단점
  - 업무별로 개발 로직이 추가 되어야 함
  - 개발 편의성을 위해서 공통 cache 라이브러리 제공 해야함





## 3.2 Pattern 2 : 주기 생성 데이터 캐싱

### (1) 패턴상세

- 적용사례: 일일 통계/집계 내역 캐싱

- 예시 표준 흐름도

<img src="redis-개념.assets/clip_image002-16568277345111.png" alt="img" style="zoom: 150%;" />



 ### (2) 장/단점

- 장점
  - 어플리케이션에서는 Cache에 Data를 push하는 로직이 필요없음
  - 통계, 집계 테이블 등 1개의 테이블에서 조회하는 업무에서 효율적임
- 단점
  - 테이블 단위의 전체 데이터를 로딩하므로 조회시점의 필요한 데이터 외 데이터가 메모리에 지속적으로 적재됨
  - 테이블 단위의 캐시로 데이터가 메모리에 지속적으로  적재됨
  - 보통 Cache 시스템은 테이블 간의 Join이 어려우므로 관계형 테이블 조회업무는 접합하지 않음





## 3.3 Pattern  3 : E2E 구간 성능 향상

> Frontend - Microservice - DB ) with Kafka

### (1) 패턴상세

- 적용사례: E2E 구간의 대량 요청 캐싱 with Kafka

- 예시 표준 흐름도

<img src="redis-개념.assets/clip_image004.png" alt="img" style="zoom: 150%;" />



### (2) 장/단점

- 장점
  - API 조회 결과의 정합성이 필요한 경우(휘발성 데이터는 해당없음)  Cache입력하기 위해 Kafka로  publish 함
  - Cache 전송시 데이터 유실없이 데이터 영구 저장이 꼭 필요한 경우(Persitent 성격의 데이터) 에 한해서 사용함 

- 단점
  - 추가적인 Kafka 리소스가 필요함
  - 업무별로 개발 로직이 추가되어야 함
  - 개발 편의성을 위해 공통 Cache 라이브러리를 제공해야 함

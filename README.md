# Kakfa / Redis on Kubernetes

> ktds Container 기반 Kafka, Redis 교육자료

본 교육은 Container를 기반으로 한 Kafka, Redis 를 학습하는 과정으로 kubernetes 환경에서 kafka/Redis  install수행하는 방법과  각각 모니터 솔루션을 설치하는 방법을 알아보며 Spring Boot / Python 으로 실습한다.

문의: 송양종( yj.song@kt.com / ssongmantop@gmail.com )








# 1. 시작전에 ( [가이드 문서 보기](./beforebegin/beforebegin.md) )  



## 1) 실습 환경 준비(개인PC)

- WSL2 설치
- Docker Desktop 설치
- MobaxTerm 설치
- Typora 설치
- 교육자료 Download






# 2. Kafka 개념 ( [가이드 문서 보기](./kafka/1.kafka-개념.md) )  



## 1) Kafka 개요

- kafka 개요와 특징확인



## 2) Kafka 기본

- Kafka 구성요소인 Broker, Message, Producer, Consumer ,Topic 의 Concept 대해 확인
- Broker
  - Producer와 Consumer 사이에 존재하는 Broker 확인
- Partition과 Consumer Group
  - 병렬처리를 가능하게 하는 Partition과 수신 애플리케이션을 담당하는 Consumer Group 
- Offset 관리
  - Conumer Group 단위로 Offset 을 관리
- Producer/Consumer Partitioning
  - Partion 별로 메세지를 수신/ 발신 한느 방법에 대한 개념 확인



## 3) Kafka Replication

- Replicas
  -  Broker 장애시 수신 메시지 분실 방지를 위한 복제(Replication)
- ISR(In-Sync Replica)
  - ISR은 **현재 동기화 상태에 있는** **리플리케이션**
- Producer Ack
  - Replication 구조에서 Producer 메시지 송신 타이밍을 결정하는 Ack 설정






# 3. Kafka Hands-in ( [가이드 문서 보기](./kafka/2.kafka-hands-in.md) )  



## 1) Strimzi

- Strimzi /  Strimzi Operator 란?




## 2) Strimzi Operator install

- Strmzi download 및 install 방법 



## 3) Kafka Cluster 생성

- Kafka Cluster 생성
- Kafka User 생성
- Kafka Topic 생성



## 4) Accessing Kafka

- Broker 접근 방식의 이해
- Internal / External Access 이해 
- Internal Access Test
- Node Port 구성 / External Access Test



## 5) Python Test

- Python Client (Kubernetes / Docker 이용) 설치
- Internal Access  / External Access



## 6) Strimzi Clean up



## 7) Java - Spring Cloud Stream

- 개인별로 할당된 Topic 확인
- 개인별 Spring Cloud Stream 특정
- kafka-consumer 실습
- kafka-producer 실습



## 8) Rebalancing Round

- Stop The World 내용 확인
- Rebalancing 시나리오
- 시나리오 테스트 수행 





# 4. Redis 개념 ( [가이드 문서 보기](./redis/redis-개념.md) )  

## 1) Redis 개요

- Redis 개요
- Redis / Redis Cluster



## 2) Caching

- Caching 정의
- hit 관련 용어
- Cache 사용시 주의사항



## 3) Cache Pattern

- Pattern 1 : 코드성 데이터 캐싱

- Pattern 2 : 주기 생성 데이터 캐싱
- Pattern  3 : E2E 구간 성능 향상





# 5. Redis Hands-in ( [가이드 문서 보기](./redis/redis-hands-in.md) )  



## 1) Redis Install 준비



## 2) Redis Cluster Install

- helm chart download / Redis Cluster Install
- Internal Access - Redis client 실행, Redis-cluster 상태 확인, set/get 확인



## 3) Redis Install

- helm chart download / RedisInstall



## 4) Accessing Redis

- Internal Access : Redis client, set/get 확인
- External Access : Node IP 확인, Redis client 확인(Docker), set/get 확인



## 5) P3X Redis UI

- redis-ui deploy



## 6) ACL

- ACL 기본명령
- 읽기전용 계정 생성
- 특정 key만 접근 허용 권한



## 7) Java Sample

- Jedis vs Lettuce
- redis-sample 소스 확인 및 수행
- CRUD 테스트



## 8) Redis Clean up





# 별첨. KT Cloud Setup ( [가이드 문서 보기](./ktcloud-setup/ktcloud-setup.md) )  

## 1) 서버 생성

- k3s Cluster 용도 VM 생성
- k3s 설치



## 2) Strimzi on KTCloud

- Strimzi Cluster Operator Install

- Kafka Cluster 생성

- KafkaUser / KafkaTopic 생성

- Accessing Kafka / Internal Access / External Access(Node Port)

- Monitoring 환경구축 (Kafka Exporter / Prometheus / Grafana )

  

## 3) Redis on KT Cloud

- helm install
- Redis Cluster / Redis Install
- P3X Redis UI
- ACL
- Java Sample




# Redis Cluster Test





# 1. Redis Cluster Install

kubernetes 기반에서 Redis 를 설치해보자.

참조link : https://github.com/bitnami/charts/tree/master/bitnami/redis-cluster



## 1) helm chart download



### (1) Repo add

redis-cluster chart 를 가지고 있는 bitnami repogistory 를  helm repo 에 추가한다.

```sh
$ helm repo add bitnami https://charts.bitnami.com/bitnami
$ helm repo list
NAME    URL
bitnami https://charts.bitnami.com/bitnami
```



### (2) Helm Search

추가된 bitnami repo에서 redis-cluster 를 찾는다.

```sh
$ helm search repo redis
NAME                                            CHART VERSION   APP VERSION     DESCRIPTION
bitnami/redis                                   17.11.2         7.0.11          Redis(R) is an open source, advanced key-value ...
bitnami/redis-cluster                           8.6.1           7.0.11          Redis(R) is an open source, scalable, distribut...


# 2023.07.15
NAME                    CHART VERSION   APP VERSION     DESCRIPTION
bitnami/redis           17.11.8         7.0.12          Redis(R) is an open source, advanced key-value ...
bitnami/redis-cluster   8.6.7           7.0.12          Redis(R) is an open source, scalable, distribut...


```

우리가 사용할 redis-cluster 버젼은 chart version 8.6.1( app version: 7.0.11) 이다.



### (3) Helm Fetch

helm chart 를 fetch 받는다.

```sh
# chart 를 저장할 적당한 위치로 이동
$ mkdir -p ~/temp/helm/charts
  cd ~/temp/helm/charts

$ helm fetch bitnami/redis-cluster

$ ll
-rw-r--r-- 1 ktdseduuser ktdseduuser 105787 Jul  9 06:39 redis-cluster-8.6.6.tgz



$ tar -xzvf redis-cluster-8.6.6.tgz
...

$ cd redis-cluster

$ ls -ltr
-rw-r--r-- 1 ktdseduuser ktdseduuser   333 May 21 17:57 .helmignore
-rw-r--r-- 1 ktdseduuser ktdseduuser   225 May 21 17:57 Chart.lock
-rw-r--r-- 1 ktdseduuser ktdseduuser   747 May 21 17:57 Chart.yaml
-rw-r--r-- 1 ktdseduuser ktdseduuser 75124 May 21 17:57 README.md
drwxrwxr-x 3 ktdseduuser ktdseduuser  4096 Jun 11 09:57 charts/
drwxrwxr-x 2 ktdseduuser ktdseduuser  4096 Jun 11 09:57 img/
drwxrwxr-x 2 ktdseduuser ktdseduuser  4096 Jun 11 09:57 templates/
-rw-r--r-- 1 ktdseduuser ktdseduuser 42471 May 21 17:57 values.yaml


```





## 2) Install

> without pv





### (1) Helm Install

```sh
$ cd  ~/temp/helm/charts/redis-cluster

## dry-run 으로 실행
$ helm -n redis-system install my-release bitnami/redis-cluster \
    --set password=new1234 \
    --set persistence.enabled=false \
    --set metrics.enabled=false \
    --set cluster.nodes=6 \
    --set cluster.replicas=1 \
    --dry-run=true
    
    
    ### 추가옵션 ###
    ### 아래와 같이설정되면 svc 명으로 redirect 됨
    --set cluster.externalAccess.enabled=true \
    --set cluster.externalAccess.service.type=LoadBalancer \
    --set cluster.externalAccess.service.loadBalancerIP[0]=my-release-redis-cluster-0-svc \
    --set cluster.externalAccess.service.loadBalancerIP[1]=my-release-redis-cluster-1-svc \
    --set cluster.externalAccess.service.loadBalancerIP[2]=my-release-redis-cluster-2-svc \
    --set cluster.externalAccess.service.loadBalancerIP[3]=my-release-redis-cluster-3-svc \
    --set cluster.externalAccess.service.loadBalancerIP[4]=my-release-redis-cluster-4-svc \
    --set cluster.externalAccess.service.loadBalancerIP[5]=my-release-redis-cluster-5-svc \
    

## 실행
$ helm -n redis-system install my-release . \
    --set password=new1234 \
    --set persistence.enabled=false \
    --set metrics.enabled=false \
    --set cluster.nodes=6 \
    --set cluster.replicas=1 


# [참고]
    # node port 접속시 - redis cluster  에서는 의미 없다.
    --set service.type=NodePort \
    --set service.nodePorts.redis=32300 \


NAME: my-release
LAST DEPLOYED: Sun Jun 11 09:58:55 2023
NAMESPACE: redis-system
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
CHART NAME: redis-cluster
CHART VERSION: 8.6.2
APP VERSION: 7.0.11** Please be patient while the chart is being deployed **


To get your password run:
    export REDIS_PASSWORD=$(kubectl get secret --namespace "redis-system" my-release-redis-cluster -o jsonpath="{.data.redis-password}" | base64 -d)

You have deployed a Redis&reg; Cluster accessible only from within you Kubernetes Cluster.INFO: The Job to create the cluster will be created.To connect to your Redis&reg; cluster:

1. Run a Redis&reg; pod that you can use as a client:
kubectl run --namespace redis-system my-release-redis-cluster-client --rm --tty -i --restart='Never' \
 --env REDIS_PASSWORD=$REDIS_PASSWORD \
--image docker.io/bitnami/redis-cluster:7.0.11-debian-11-r12 -- bash

2. Connect using the Redis&reg; CLI:

redis-cli -c -h my-release-redis-cluster -a $REDIS_PASSWORD


## 확인
$ helm -n redis-system ls
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
my-release      redis-system    1               2023-06-11 09:58:55.994892092 +0000 UTC deployed        redis-cluster-8.6.2     7.0.11


## 상태확인
$ helm -n redis-system status my-release

# helm 삭제
$ helm -n redis-system delete my-release

```





### (2) pod/svc 확인

```sh
## redis cluster 를 구성하고 있는 pod 를 조회
$ kubectl -n redis-system get pod -o wide
NAME                         READY   STATUS    RESTARTS   AGE   IP           NODE        NOMINATED NODE   READINESS GATES
my-release-redis-cluster-0   1/1     Running   0          57s   10.42.0.27   bastion03   <none>           <none>
my-release-redis-cluster-1   1/1     Running   0          56s   10.42.0.26   bastion03   <none>           <none>
my-release-redis-cluster-2   1/1     Running   0          56s   10.42.0.28   bastion03   <none>           <none>
my-release-redis-cluster-3   1/1     Running   0          56s   10.42.0.29   bastion03   <none>           <none>
my-release-redis-cluster-4   1/1     Running   0          56s   10.42.0.30   bastion03   <none>           <none>
my-release-redis-cluster-5   1/1     Running   0          56s   10.42.0.31   bastion03   <none>           <none>
...



$ kubectl -n redis-system get svc
NAME                                TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)              AGE
my-release-redis-cluster-headless   ClusterIP   None           <none>        6379/TCP,16379/TCP   68s
my-release-redis-cluster            ClusterIP   10.43.13.151   <none>        6379/TCP             68s



```





### (3) Helm Install with pv/pvc



#### Install

```sh

## 실행 with persistence
$ helm -n redis-system install my-release bitnami/redis-cluster \
    --set password=new1234 \
    --set persistence.enabled=true \
    --set metrics.enabled=false \
    --set cluster.nodes=6 \
    --set cluster.replicas=1 
    
    
    
    
    
## 확인
$ helm -n redis-system ls
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
my-release      redis-system    1               2023-07-15 01:22:47.936886236 +0000 UTC deployed        redis-cluster-8.6.7     7.0.12


## 삭제
$ helm -n redis-system delete my-release


```





#### pv/pvc 확인

일반적으로 storageClass 가 local 환경의 특정 node 에 hostpath로 pv를 생성한다.

```yaml
$ krs get pv
NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM                                                STORAGECLASS   REASON   AGE
pvc-559bbd22-000a-4edb-a1e7-ed2bc2fc2b14   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-1   local-path              18m
pvc-2c0938b9-cc18-4d09-88ef-cd78e9684fad   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-3   local-path              18m
pvc-9b391772-e63d-479c-8d7c-0dcf052bcfcf   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-2   local-path              18m
pvc-bd18aed3-ffcc-4b57-8634-50491b19741e   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-0   local-path              18m
pvc-e0a98d7d-fb5c-499d-b5c7-e4d7126b7aec   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-4   local-path              18m
pvc-adc273c4-8931-4024-8aad-0a0557d04af2   8Gi        RWO            Delete           Bound       redis-system/redis-data-my-release-redis-cluster-5   local-path              18m
redis-pv-0                                 1Gi        RWO            Retain           Available                                                        manual                  2m22s


$ krs get pvc
NAME                                    STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
redis-data-my-release-redis-cluster-1   Bound    pvc-559bbd22-000a-4edb-a1e7-ed2bc2fc2b14   8Gi        RWO            local-path     18m
redis-data-my-release-redis-cluster-3   Bound    pvc-2c0938b9-cc18-4d09-88ef-cd78e9684fad   8Gi        RWO            local-path     18m
redis-data-my-release-redis-cluster-2   Bound    pvc-9b391772-e63d-479c-8d7c-0dcf052bcfcf   8Gi        RWO            local-path     18m
redis-data-my-release-redis-cluster-0   Bound    pvc-bd18aed3-ffcc-4b57-8634-50491b19741e   8Gi        RWO            local-path     18m
redis-data-my-release-redis-cluster-4   Bound    pvc-e0a98d7d-fb5c-499d-b5c7-e4d7126b7aec   8Gi        RWO            local-path     18m
redis-data-my-release-redis-cluster-5   Bound    pvc-adc273c4-8931-4024-8aad-0a0557d04af2   8Gi        RWO            local-path     18m

```















## 3) Internal Access

redis client를 cluster 내부에서 실행후 접근하는 방법을 알아보자.

### (1) Redis client 실행

먼저 아래와 같이 동일한 Namespace 에 redis-client 를 실행한다.

```sh
## redis-client 용도로 deployment 를 실행한다.
$ kubectl -n redis-system create deploy redis-client --image=docker.io/bitnami/redis-cluster:6.2.7-debian-11-r3 -- sleep 365d
deployment.apps/redis-client created


## redis client pod 확인
$ kubectl -n redis-system get pod
NAME                            READY   STATUS    RESTARTS   AGE
redis-client-7cdd56bb6c-njjls   1/1     Running   0          5s     <--- redis client pod


# 약 20초 정도 소요됨


## redis-client pod 내부로 접근한다.
$ kubectl -n redis-system exec -it deploy/redis-client -- bash
I have no name!@redis-client-69dcc9c76d-kc8r9:/$    # <-- 이런 Prompt가 나오면 정상

```



### (2) Redis-cli 으로 확인

#### Redis-cli 실행

```sh
## redis-client pod 내부에서...

## service 명으로 cluster mode 접근
$ redis-cli -h my-release-redis-cluster -c -a new1234

## cluster node 를 확인
my-release-redis-cluster:6379> cluster nodes
e69395428d53c0994dcd5265ee5e9148e3b5cf4a 10.42.0.186:6379@16379 slave 3b517eae7922e1bca8e31d54ad5d200ba8fd1bc6 0 1689598090384 1 connected
e724fccb95e0eccc1087ab5125f729c276a99b6a 10.42.0.181:6379@16379 master - 0 1689598089371 2 connected 5461-10922
be02592f92c901525db78d86b1127bded541db55 10.42.0.182:6379@16379 slave 07d57665be56042b89006c11c71c5bec9e61f321 0 1689598088367 3 connected
3b517eae7922e1bca8e31d54ad5d200ba8fd1bc6 10.42.0.185:6379@16379 myself,master - 0 1689598090000 1 connected 0-5460
07d57665be56042b89006c11c71c5bec9e61f321 10.42.0.184:6379@16379 master - 0 1689598088000 3 connected 10923-16383
2d39307a7a391bcc90446b1e0bc173e6852732ea 10.42.0.187:6379@16379 slave e724fccb95e0eccc1087ab5125f729c276a99b6a 0 1689598087361 2 connected

## master 3개, slave가 3개 사용하는 모습을 볼 수가 있다.



## cluster info 확인
my-release-redis-cluster:6379> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:6
cluster_size:3
cluster_current_epoch:6
cluster_my_epoch:1
cluster_stats_messages_ping_sent:1416
cluster_stats_messages_pong_sent:1466
cluster_stats_messages_sent:2882
cluster_stats_messages_ping_received:1461
cluster_stats_messages_pong_received:1416
cluster_stats_messages_meet_received:5
cluster_stats_messages_received:2882
total_cluster_links_buffer_limit_exceeded:0

## cluster state 가 OK 인 것을 확인할 수 있다.

```



#### set / get 확인

```sh
# Redis cli 에서...


## set 명령 수행
my-release-redis-cluster:6379> set a 1
-> Redirected to slot [15495] located at 10.42.0.28:6379
OK
10.42.0.28:6379> set b 2
-> Redirected to slot [3300] located at 10.42.0.27:6379
OK
10.42.0.27:6379> set c 3
-> Redirected to slot [7365] located at 10.42.0.26:6379
OK
10.42.0.26:6379> set d 4
-> Redirected to slot [11298] located at 10.42.0.28:6379
OK
10.42.0.28:6379> set e 5
OK
10.42.0.28:6379> set f 6
-> Redirected to slot [3168] located at 10.42.0.27:6379
OK

## Set 명령수행시 master node 를 변경하면서 set 하는 모습을 확인할 수 있다.



# get 명령 수행
10.42.0.27:6379> get a
-> Redirected to slot [15495] located at 10.42.0.28:6379
"1"
10.42.0.28:6379> get b
-> Redirected to slot [3300] located at 10.42.0.27:6379
"2"
10.42.0.27:6379> get c
-> Redirected to slot [7365] located at 10.42.0.26:6379
"3"
10.42.0.26:6379> get d
-> Redirected to slot [11298] located at 10.42.0.28:6379
"4"
10.42.0.28:6379> get e
"5"
10.42.0.28:6379> get f
-> Redirected to slot [3168] located at 10.42.0.27:6379
"6"


## get 명령을 실행하면 해당 데이터가 존재하는 master pod 로 redirectred 되는 것을 확인할 수 있다.


# 테스트 완료후 
# Ctrl+C ,  Ctrl+D 명령으로 Exit 하자.

```



### (3) python 으로 확인

Kubernetes Cluster 내에서 redis 접근 가능여부를 확인하기 위해 python 을 설치후 redis 에 connect 해 보자.



#### python  설치

```sh
# python deploy
$ kubectl -n redis-system create deploy python --image=python:3.9 -- sleep 365d


# 설치진행 확인
$ kubectl -n redis-system get pod
...
python-fb57f7bd4-4w6pz                       1/1     Running   0              32s
...

## READY 상태가 1/1 로 변할때까지 대기...
## 약 1분 소요


# python pod 내부로 진입( bash 명령 수행)
$ kubectl -n redis-system exec -it deploy/python -- bash
root@python-7d59455985-ml8vw:/#                  <-- 이런 prompt 가 정상


```



#### python library install

kafka 에 접근하기 위해서 kafka-python 을 설치해야 한다.

```bash
# python pod 내부에서

$ pip install redis-py-cluster

Collecting redis-py-cluster
  Downloading redis_py_cluster-2.1.3-py2.py3-none-any.whl (42 kB)
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 42.6/42.6 kB 5.5 MB/s eta 0:00:00
Collecting redis<4.0.0,>=3.0.0
  Downloading redis-3.5.3-py2.py3-none-any.whl (72 kB)
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ 72.1/72.1 kB 10.8 MB/s eta 0:00:00
Installing collected packages: redis, redis-py-cluster
  Attempting uninstall: redis
    Found existing installation: redis 4.6.0
    Uninstalling redis-4.6.0:
      Successfully uninstalled redis-4.6.0
Successfully installed redis-3.5.3 redis-py-cluster-2.1.3


```



#### [참고] redis host 확인

```sh
# internal 접근을 위한 host 확인
# nc 명령으로 접근가능여부를 확인할 수 있다.

$ apt update
$ apt install netcat

$ nc -zv my-release-redis-cluster.redis-system.svc 6379

Connection to my-release-redis-cluster.redis-system.svc (10.43.47.183) 6379 port [tcp/redis] succeeded!

$ nc -zv my-release-redis-cluster-0-svc.redis-system.svc 6379
$ nc -zv my-release-redis-cluster-1-svc.redis-system.svc 6379
$ nc -zv my-release-redis-cluster-2-svc.redis-system.svc 6379
$ nc -zv my-release-redis-cluster-3-svc.redis-system.svc 6379
$ nc -zv my-release-redis-cluster-4-svc.redis-system.svc 6379
$ nc -zv my-release-redis-cluster-5-svc.redis-system.svc 6379


```



#### redis 확인

consumer 실행을 위해서 python cli 환경으로 들어가자.

```sh
# python pod 내부에서
$ python

Python 3.9.13 (main, May 28 2022, 13:56:03)
[GCC 10.2.1 20210110] on linux
Type "help", "copyright", "credits" or "license" for more information.
>>>

```



CLI 환경에서 아래  Python 명령을 하나씩 실행해 보자.

```python
from rediscluster import RedisCluster



startup_nodes = [{"host":"my-release-redis-cluster", "port":"6379"}]
rc = RedisCluster(startup_nodes=startup_nodes, 
                      decode_responses=True, 
                      skip_full_coverage_check=True,
                     password="new1234")

print(rc.cluster('slots'))

'''
{
(0, 5460): {'master': ('my-release-redis-cluster-0-svc', 6379), 'slaves': [('my-release-redis-cluster-4-svc', 6379)]}, 
(5461, 10922): {'master': ('my-release-redis-cluster-1-svc', 6379), 'slaves': [('my-release-redis-cluster-5-svc', 6379)]}, 
(10923, 16383): {'master': ('my-release-redis-cluster-2-svc', 6379), 'slaves': [('my-release-redis-cluster-3-svc', 6379)]}
}
'''


# redis set
rc.set("a", "python1")
rc.set("b", "python2")
rc.set("c", "python3")

# redis get
rc.get("a")
rc.get("b")
rc.get("c")

# delete key
rc.delete("c")

# 기타
rc.set('foo','bar')
print(rc.get('foo'))
key_list  = rc.keys("*")
print(key_list)



# 10000건을 1초에 한번씩 발송해보자.
from time import sleep
for i in range(10000):
    print(i)
    sleep(1)
    rc.get("a")
    rc.get("b")
    rc.get("c")

# 테스트를 끝내려면 Ctrl + C 로 중지하자.

```









## 4) External Access

Redis Cluster 는 K8s 내부에서만 사용가능한 주소체계로 redirect 되므로 k8s 외부에서는 접근이 불가능하다.



## 5) Clean Up

```sh
# 1) helm 삭제
# helm delete 명령을 이용하면 helm chart 로 설치된 모든 리소스가 한꺼번에 삭제된다.
$ helm -n redis-system delete my-release
$ helm -n redis-system ls


# 2) helm chart 삭제
$ rm -rf ~/temp/helm/charts/redis-cluster/
$ rm -rf ~/temp/helm/charts/redis-cluster-8.6.2.tgz


## 3) redis-client 삭제
$ kubectl -n redis-system delete deploy/redis-client
$ kubectl -n redis-system get all


```















# 2. FailOver Test

Master node 1개를 down 하여 추이를 지켜 본다.



## 1) Master node down

### (1) master node down

```sh
$ redis-cli -h my-release-redis-cluster -c -a new1234

my-release-redis-cluster:6379> cluster nodes
9fa9dc6cab3398624df40713b4bc675b1322184c 10.42.0.129:6379@16379 myself,master - 0 1689167734000 3 connected 10923-16383
37ede9c99774fcdb6ad3b34f7d681061244fba93 10.42.0.128:6379@16379 master - 0 1689167736973 2 connected 5461-10922
bf04da290df7003c4dacb45cbdf2ac40d696663c 10.42.0.131:6379@16379 slave 9d6c30fdada95856e40e1634c51113a85dd50c53 0 1689167738983 1 connected
77b47a2e6cd38a19ce7b71eaf142b0ef87de16b9 10.42.0.132:6379@16379 slave 37ede9c99774fcdb6ad3b34f7d681061244fba93 0 1689167738000 2 connected
ff84e2066b155907cf0981f9c37bbc2686d8c880 10.42.0.130:6379@16379 slave 9fa9dc6cab3398624df40713b4bc675b1322184c 0 1689167737000 3 connected
9d6c30fdada95856e40e1634c51113a85dd50c53 10.42.0.127:6379@16379 master - 0 1689167737978 1 connected 0-5460


10.42.0.129:6379> get a
"1"

10.42.0.129:6379> get b
-> Redirected to slot [3300] located at 10.42.0.127:6379
"2"

10.42.0.127:6379>  get c
-> Redirected to slot [7365] located at 10.42.0.128:6379
"3"


# c key 가 존재하는 10.42.0.128 를 down 시켜 보자.

###
down 시도
###



```



slave --> master 로 변경된 node log 를확인해 보자.

```sh

1:S 12 Jul 2023 13:18:41.965 # Connection with master lost.
1:S 12 Jul 2023 13:18:41.965 * Caching the disconnected master state.
1:S 12 Jul 2023 13:18:41.965 * Reconnecting to MASTER 10.42.0.128:6379
1:S 12 Jul 2023 13:18:41.965 * MASTER <-> REPLICA sync started
1:S 12 Jul 2023 13:18:41.965 # Error condition on socket for SYNC: Connection refused
1:S 12 Jul 2023 13:18:42.362 * Connecting to MASTER 10.42.0.128:6379
1:S 12 Jul 2023 13:18:42.362 * MASTER <-> REPLICA sync started

# 20초 이후 Cluster fail
1:S 12 Jul 2023 13:19:02.420 * FAIL message received from bf04da290df7003c4dacb45cbdf2ac40d696663c about 37ede9c99774fcdb6ad3b34f7d681061244fba93
1:S 12 Jul 2023 13:19:02.420 # Cluster state changed: fail
1:S 12 Jul 2023 13:19:02.518 # Start of election delayed for 532 milliseconds (rank #0, offset 400).
1:S 12 Jul 2023 13:19:03.121 # Starting a failover election for epoch 7.
1:S 12 Jul 2023 13:19:03.128 # Failover election won: I'm the new master.
1:S 12 Jul 2023 13:19:03.128 # configEpoch set to 7 after successful failover

# 여기서부터 Master 로 변경되었다.
1:M 12 Jul 2023 13:19:03.128 * Discarding previously cached master state.
1:M 12 Jul 2023 13:19:03.128 # Setting secondary replication ID to dabdb3cdaa7ad4a95b38120995ba3d7884c549d5, valid up to offset: 401. New replication ID is a362761f13e0ce91bce8f643f817d56fe0afbc36
1:M 12 Jul 2023 13:19:03.129 # Cluster state changed: ok

# cluster ok

```







### (2) redis-cli 확인



계속 get 명령 수행해보자.

```sh


10.42.0.128:6379>  get c
Error: Server closed the connection
10.42.0.128:6379>  get c
^[[A
Could not connect to Redis at 10.42.0.128:6379: No route to host
(34.46s)
not connected> get c
Could not connect to Redis at 10.42.0.128:6379: No route to host
(3.07s)


# 10.42.0.128 서버가 다운되어 있으므로 connection 이 close 되어 버렸다.


```



새로운 connection 으로 접근해보자.

```sh
$ redis-cli -h my-release-redis-cluster -c -a new1234

10.42.0.132:6379> cluster nodes
bf04da290df7003c4dacb45cbdf2ac40d696663c 10.42.0.131:6379@16379 slave 9d6c30fdada95856e40e1634c51113a85dd50c53 0 1689168015927 1 connected
37ede9c99774fcdb6ad3b34f7d681061244fba93 10.42.0.128:6379@16379 master,fail - 1689167922262 1689167919000 2 connected
9fa9dc6cab3398624df40713b4bc675b1322184c 10.42.0.129:6379@16379 master - 0 1689168017940 3 connected 10923-16383
77b47a2e6cd38a19ce7b71eaf142b0ef87de16b9 10.42.0.132:6379@16379 myself,master - 0 1689168016000 7 connected 5461-10922
9d6c30fdada95856e40e1634c51113a85dd50c53 10.42.0.127:6379@16379 master - 0 1689168017000 1 connected 0-5460
ff84e2066b155907cf0981f9c37bbc2686d8c880 10.42.0.130:6379@16379 slave 9fa9dc6cab3398624df40713b4bc675b1322184c 0 1689168016935 3 connected



```

10.42.0.132 node 가 slave 에서 master 로 승격되었다.

하지만 기존에 down된 node 는 fail 로 남아 있다.

get 명령을 수행해보자.

```sh

10.42.0.132:6379> get a
-> Redirected to slot [15495] located at 10.42.0.129:6379
"1"
10.42.0.129:6379>
10.42.0.129:6379> get b
-> Redirected to slot [3300] located at 10.42.0.127:6379
"2"
10.42.0.127:6379>
10.42.0.127:6379> get c
-> Redirected to slot [7365] located at 10.42.0.132:6379
"3"
```



새로운 connection 은 문제가 없다.

Fail Node 는 어떻게 정상화 시킬 수 있을까?  (task 1)

기존 connection 에서 Redis 가 정상화 되었을때 문제 없이 connect 되어야 하는데 어떻게 해결 할수 있을까?  (task 2)







### (3) python 테스트



```python



# redis set
rc.set("a", "python1")
rc.set("b", "python2")
rc.set("c", "python3")



# 10000건을 1초에 한번씩 발송해보자.
from time import sleep

for i in range(10000):
    print(i)
    sleep(1)
    rc.get("a")
    rc.get("b")
    rc.get("c")
    

# 1) Test1
# down 발생 - slave 가 master 로 승격되는 경우 (pvc가 없을경우)
# 약 20초정도 이후 정상작동한다.



# 2) Test2
# down 발생
# master node 가 cluster-node-timeout(기본, 15초) 동안 반응이 없으면 slave 가 master 로 승격된다.





# 2) Test2
# down 발생 - master가 그냥 재기동 된다면
# 약 40초정도 이후 정상작동한다.

```









## 2) Fail Node 확인



### (1) pod not ready 확인

pod 를 확인해 보자.

```sh
$ krs get pod
NAME                            READY   STATUS    RESTARTS   AGE
python-7d59455985-scgxb         2/2     Running   0          3d8h
my-release-redis-cluster-2      1/1     Running   0          36m
my-release-redis-cluster-3      1/1     Running   0          36m
my-release-redis-cluster-4      1/1     Running   0          36m
my-release-redis-cluster-5      1/1     Running   0          36m
my-release-redis-cluster-0      1/1     Running   0          36m
redis-client-69dcc9c76d-g6sms   1/1     Running   0          35m
my-release-redis-cluster-1      0/1     Running   0          22m

```

down 이후 자동 재기동된 pod가 readiness 를 통과하지 못했다. 

원인이 무엇인지 확인해보자.



아래는 readinessprobe 명령이다.

```yaml
                                                                                                           │
│     readinessProbe:                                                                                                            │
│       exec:                                                                                                                    │
│         command:                                                                                                               │
│         - sh                                                                                                                   │
│         - -c                                                                                                                   │
│         - /scripts/ping_readiness_local.sh 1                                                                                   │
│       failureThreshold: 5                                                                                                      │
│       initialDelaySeconds: 5                                                                                                   │
│       periodSeconds: 5                                                                                                         │
│       successThreshold: 1                                                                                                      │
│       timeoutSeconds: 2  
```



ping_readiness_local.sh 값을 확인해보자.

```shell
$ cat /scripts/ping_readiness_local.sh
#!/bin/sh
set -e

REDIS_STATUS_FILE=/tmp/.redis_cluster_check
if [ ! -z "$REDIS_PASSWORD" ]; then export REDISCLI_AUTH=$REDIS_PASSWORD; fi;
response=$(
  timeout -s 15 $1 \
  redis-cli \
    -h localhost \
    -p $REDIS_PORT_NUMBER \
    ping
)
if [ "$?" -eq "124" ]; then
  echo "Timed out"
  exit 1
fi
if [ "$response" != "PONG" ]; then
  echo "$response"
  exit 1
fi
if [ ! -f "$REDIS_STATUS_FILE" ]; then
  response=$(
    timeout -s 15 $1 \
    redis-cli \
      -h localhost \
      -p $REDIS_PORT_NUMBER \
      CLUSTER INFO | grep cluster_state | tr -d '[:space:]'
  )
  if [ "$?" -eq "124" ]; then
    echo "Timed out"
    exit 1
  fi
  if [ "$response" != "cluster_state:ok" ]; then
    echo "$response"
    exit 1
  else
    touch "$REDIS_STATUS_FILE"
  fi

```





fail node 에서 확인해보자.

```sh


$ sh -c "/scripts/ping_readiness_local.sh 1"
cluster_state:fail

# 실패한다.



# 패스워드는 정상
$ echo $REDIS_PASSWORD
new1234

$ echo $REDIS_PORT_NUMBER
6379


# ping pong test
$ redis-cli \
    -h localhost \
    -p 6379 \
    ping
PONG


# cluster info 확인 1
$ redis-cli \
      -h localhost \
      -p $REDIS_PORT_NUMBER \
      CLUSTER INFO

cluster_state:fail
cluster_slots_assigned:0
cluster_slots_ok:0
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:1
cluster_size:0
cluster_current_epoch:0
cluster_my_epoch:0
cluster_stats_messages_sent:0
cluster_stats_messages_received:0
total_cluster_links_buffer_limit_exceeded:0


# cluster info 확인 2
$ redis-cli \
      -h localhost \
      -p $REDIS_PORT_NUMBER \
      CLUSTER INFO | grep cluster_state | tr -d '[:space:]'

cluster_state:fail



```



결국 cluster_state 가 실패하여 not ready 되었다.





### (2) container 시작 명령 확인



```yaml

  containers:
  - args:
    - |
      # Backwards compatibility change
      if ! [[ -f /opt/bitnami/redis/etc/redis.conf ]]; then
          echo COPYING FILE
          cp  /opt/bitnami/redis/etc/redis-default.conf /opt/bitnami/redis/etc/redis.conf
      fi
      pod_index=($(echo "$POD_NAME" | tr "-" "\n"))
      pod_index="${pod_index[-1]}"
      if [[ "$pod_index" == "0" ]]; then
        export REDIS_CLUSTER_CREATOR="yes"
        export REDIS_CLUSTER_REPLICAS="1"
      fi
      /opt/bitnami/scripts/redis-cluster/entrypoint.sh /opt/bitnami/scripts/redis-cluster/run.sh
    command:
    - /bin/bash
    - -c

```





### (3) Fail node 확인

```sh


10.42.0.132:6379> cluster nodes
bf04da290df7003c4dacb45cbdf2ac40d696663c 10.42.0.131:6379@16379 slave 9d6c30fdada95856e40e1634c51113a85dd50c53 0 1689169951000 1 connected
37ede9c99774fcdb6ad3b34f7d681061244fba93 10.42.0.128:6379@16379 master,fail - 1689167922262 1689167919000 2 connected
9fa9dc6cab3398624df40713b4bc675b1322184c 10.42.0.129:6379@16379 master - 0 1689169953754 3 connected 10923-16383
77b47a2e6cd38a19ce7b71eaf142b0ef87de16b9 10.42.0.132:6379@16379 myself,master - 0 1689169951000 7 connected 5461-10922
9d6c30fdada95856e40e1634c51113a85dd50c53 10.42.0.127:6379@16379 master - 0 1689169952749 1 connected 0-5460
ff84e2066b155907cf0981f9c37bbc2686d8c880 10.42.0.130:6379@16379 slave 9fa9dc6cab3398624df40713b4bc675b1322184c 0 1689169952000 3 connected

```









참고: 

https://mozi.tistory.com/382



### (4) FiailOver with meet-replicate

* 순서

```
1) forget 명령으로 해당노드를 group에서 제거한다.
2) meet 명령으로 node 추가
   - 일반적으로 master node 로 추가된다.  그러므로 slave 로 변경해 줘야 한다.
3) replicate 명령으로 slave 설정을 한다.
```



```sh

# 작업전
$ cluster nodes
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689391983000 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689391981000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 myself,master - 0 1689391982000 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689391983141 7 connected
217cf57cc8a39cac12dd9d614317ad55fe4c57e4 10.42.0.171:6379@16379 master,fail - 1689391538631 0 0 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689391984146 1 connected


# 1) forget
$ redis-cli -h localhost -a $REDIS_PASSWORD CLUSTER FORGET 217cf57cc8a39cac12dd9d614317ad55fe4c57e4
  
# 2) meet
$ redis-cli -h my-release-redis-cluster -a $REDIS_PASSWORD CLUSTER meet 10.42.0.174 6379
  
# 3) replicate
$ redis-cli -h localhost -c -a $REDIS_PASSWORD CLUSTER REPLICATE 12a06ae7cd0ae7efd267814e28a83ff7824b8178


# 작업후
$ cluster nodes
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689391225000 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689391224000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 myself,master - 0 1689391226000 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689391226830 7 connected
217cf57cc8a39cac12dd9d614317ad55fe4c57e4 10.42.0.171:6379@16379 master,fail - 1689390078333 0 0 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689391224817 1 connected

```





### (5) FiailOver with add-node

* 순서

```
1) forget 명령으로 해당노드를 group에서 제거한다.
2) add-node 옵션으로 Slave Node 추가
```



```sh

# 샘플
$ redis-cli --cluster \
    add-node 127.0.0.1:7001 127.0.0.1:7000 \
    --cluster-slave 869859e396c881b3c26f2a386c1495235225b57b

# 작업전
$ cluster nodes
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 master - 0 1689469636411 8 connected 0-5460
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689469637000 7 connected 10923-16383
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689469637417 2 connected 5461-10922
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 master,fail - 1689469515457 1689469511429 1 connected
da5aaba2df35da46204ff96eb4be853ccd507606 10.42.0.174:6379@16379 slave 12a06ae7cd0ae7efd267814e28a83ff7824b8178 0 1689469636000 2 connected
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 myself,slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689469635000 7 connected


# 1) forget
## 모든 node 에서 1분이내에 수행해야 한다.
## 그렇지 않으면 gosship protocol 에의해서 원복된다.

$ redis-cli CLUSTER FORGET a2e3366d2310533a6cab5181afa197c93a5904a3
OK


# 2) add-node
## format
## [슬레이브 IP:PORT] , [클러스터 노드 IP:PORT], [--cluster-slave 옵션], [마스터가 될 노드 ID]

$ redis-cli -a new1234 \
    --cluster add-node 10.42.0.175:6379 10.42.0.172:6379 \
    --cluster-slave b109ed74a0e639d34067d1bf78a860931d0ee941


# 작업후
$ cluster nodes
ac04246dfccf850cbdf78565a52f131b1160233a 10.42.0.175:6379@16379 slave b109ed74a0e639d34067d1bf78a860931d0ee941 0 1689470266001 8 connected
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689470264000 7 connected
da5aaba2df35da46204ff96eb4be853ccd507606 10.42.0.174:6379@16379 slave 12a06ae7cd0ae7efd267814e28a83ff7824b8178 0 1689470264996 2 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 myself,master - 0 1689470262000 8 connected 0-5460
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689470264000 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689470263990 7 connected 10923-16383

```





 







# 3. Cluster 명령들



참고링크 : 

https://redis.io/commands/cluster-addslots/

https://medium.com/garimoo/redis-documentation-2-%EB%A0%88%EB%94%94%EC%8A%A4-%ED%81%B4%EB%9F%AC%EC%8A%A4%ED%84%B0-%ED%8A%9C%ED%86%A0%EB%A6%AC%EC%96%BC-911ba145e63







## 1) --cluster help



```sh
$ redis-cli --cluster help


Cluster Manager Commands:
  create         host1:port1 ... hostN:portN
                 --cluster-replicas <arg>
  check          host:port
                 --cluster-search-multiple-owners
  info           host:port
  fix            host:port
                 --cluster-search-multiple-owners
                 --cluster-fix-with-unreachable-masters
  reshard        host:port
                 --cluster-from <arg>
                 --cluster-to <arg>
                 --cluster-slots <arg>
                 --cluster-yes
                 --cluster-timeout <arg>
                 --cluster-pipeline <arg>
                 --cluster-replace
  rebalance      host:port
                 --cluster-weight <node1=w1...nodeN=wN>
                 --cluster-use-empty-masters
                 --cluster-timeout <arg>
                 --cluster-simulate
                 --cluster-pipeline <arg>
                 --cluster-threshold <arg>
                 --cluster-replace
  add-node       new_host:new_port existing_host:existing_port
                 --cluster-slave
                 --cluster-master-id <arg>
  del-node       host:port node_id
  call           host:port command arg arg .. arg
                 --cluster-only-masters
                 --cluster-only-replicas
  set-timeout    host:port milliseconds
  import         host:port
                 --cluster-from <arg>
                 --cluster-from-user <arg>
                 --cluster-from-pass <arg>
                 --cluster-from-askpass
                 --cluster-copy
                 --cluster-replace


```



### cluster nodes

```sh

$ redis-cli -h my-release-redis-cluster -c -a new1234


$ cluster nodes
1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379@16379 slave cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 0 1689085764198 3 connected
5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379@16379 slave fbdec1c82805e36da85530f0451a7ebcc63ac458 0 1689085765206 2 connected
fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379@16379 myself,master - 0 1689085762000 2 connected 5461-10922
63d3744de13f2898137d4b90c55cc3639cfebe49 :0@0 master,fail,noaddr - 1689084949589 1689084947000 1 disconnected
8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379@16379 master - 0 1689085764000 7 connected 0-5460
cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379@16379 master - 0 1689085763192 3 connected 10923-16383



# call
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster call my-release-redis-cluster-4-svc:6379 \
    --cluster-only-masters


```







### fix

```sh


  fix            host:port
                 --cluster-search-multiple-owners
                 --cluster-fix-with-unreachable-masters

# fix1
$ redis-cli -a new1234 \
    --cluster fix my-release-redis-cluster-0-svc:6379 \
    --cluster-search-multiple-owners

my-release-redis-cluster-0-svc:6379 (3da89f16...) -> 0 keys | 16384 slots | 0 slaves.
[OK] 0 keys in 1 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node my-release-redis-cluster-0-svc:6379)
M: 3da89f162fbe67e2bc76f5ae1a1301ecaee9931f my-release-redis-cluster-0-svc:6379
   slots:[0-16383] (16384 slots) master
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Check for multiple slot owners...
[OK] No multiple owners found.





# fix2
$ redis-cli -a new1234 \
    --cluster fix my-release-redis-cluster-0-svc:6379 \
    --cluster-fix-with-unreachable-masters

my-release-redis-cluster-0-svc:6379 (3da89f16...) -> 0 keys | 16384 slots | 0 slaves.
[OK] 0 keys in 1 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node my-release-redis-cluster-0-svc:6379)
M: 3da89f162fbe67e2bc76f5ae1a1301ecaee9931f my-release-redis-cluster-0-svc:6379
   slots:[0-16383] (16384 slots) master
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.




```





### rebalance

Cluste node Slot 재분배한다.

Hash Slot 의 번호는 CRC16(key) mod 16384 의 값으로 만들어진다.

Cluster 를 운영하다 보면 특정 Node 에 Slot 이 몰리 있는 경우가 있는데 

이럴 경우 특정 서버의 부하가 생겨 서비스에 장애가 발생할 수 있다.

주기적인 모니터링을 통해 Slot 을 재분배 해줘야 한다.

```sh

# format
  rebalance      host:port
                 --cluster-weight <node1=w1...nodeN=wN>
                 --cluster-use-empty-masters      <-- 추가한 Master Node 까지 Slot을 재분배
                 --cluster-timeout <arg>
                 --cluster-simulate
                 --cluster-pipeline <arg>
                 --cluster-threshold <arg>
                 --cluster-replace

# 명령어
## redis-cli --cluster rebalance [Master Node IP]:[Master Node Port]


# 샘플
## redis-cli --cluster rebalance 127.0.0.1:7000


# rebalance 1
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster rebalance my-release-redis-cluster-0-svc:6379

>>> Performing Cluster Check (using node my-release-redis-cluster-0-svc:6379)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[ERR] Not all 16384 slots are covered by nodes.

*** Please fix your cluster problems before rebalancing



# rebalance 2
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster rebalance my-release-redis-cluster-0-svc:6379
    
>>> Performing Cluster Check (using node my-release-redis-cluster-0-svc:6379)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
*** No rebalancing needed! All nodes are within the 2.00% threshold.



```









### add-node

```sh


  add-node       new_host:new_port existing_host:existing_port
                 --cluster-slave
                 --cluster-master-id <arg>
                 
                 
                 
# add-node
$ redis-cli -a new1234 \
    --cluster add-node my-release-redis-cluster-0-svc:6379 my-release-redis-cluster-4-svc:6379 \
    --cluster-slave

>>> Adding node my-release-redis-cluster-0-svc:6379 to cluster my-release-redis-cluster-4-svc:6379
>>> Performing Cluster Check (using node my-release-redis-cluster-4-svc:6379)
M: 8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379
   slots:[0-5460] (5461 slots) master
M: fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
M: cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: 5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379
   slots: (0 slots) slave
   replicates fbdec1c82805e36da85530f0451a7ebcc63ac458
S: 1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379
   slots: (0 slots) slave
   replicates cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
Automatically selected master my-release-redis-cluster-4-svc:6379
>>> Send CLUSTER MEET to node my-release-redis-cluster-0-svc:6379 to make it join the cluster.
Node my-release-redis-cluster-0-svc:6379 replied with error:
ERR Invalid node address specified: my-release-redis-cluster-4-svc:6379

# 왜 Invalid node address 라고 나올까???


$ redis-cli -a new1234 \
    --cluster add-node my-release-redis-cluster-0-svc:6379 my-release-redis-cluster-4-svc:6379 \
    --cluster-master-id cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1
    

```



```sh
# 7006 을 7005의 복제로 추가하는 샘플
$ redis-cli --cluster add-node 127.1.1.1:7006 127.1.1.1:7005 --cluster-slave

```











### check

```sh
# nodes 
$ cluster nodes
fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379@16379 master - 0 1689087098000 2 connected 5461-10922
cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379@16379 myself,master - 0 1689087097000 3 connected 10923-16383
8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379@16379 master - 0 1689087099230 7 connected 0-5460
63d3744de13f2898137d4b90c55cc3639cfebe49 :0@0 master,fail,noaddr - 1689084949651 1689084947000 1 disconnected
5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379@16379 slave fbdec1c82805e36da85530f0451a7ebcc63ac458 0 1689087100235 2 connected
1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379@16379 slave cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 0 1689087100000 3 connected



# master node check
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster check my-release-redis-cluster-4-svc:6379 \
    --cluster-search-multiple-owners

my-release-redis-cluster-4-svc:6379 (8fd49b4f...) -> 1 keys | 5461 slots | 0 slaves.
my-release-redis-cluster-1-svc:6379 (fbdec1c8...) -> 1 keys | 5462 slots | 1 slaves.
my-release-redis-cluster-2-svc:6379 (cd682076...) -> 2 keys | 5461 slots | 1 slaves.
[OK] 4 keys in 3 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node my-release-redis-cluster-4-svc:6379)
M: 8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379
   slots:[0-5460] (5461 slots) master
M: fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
M: cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: 5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379
   slots: (0 slots) slave
   replicates fbdec1c82805e36da85530f0451a7ebcc63ac458
S: 1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379
   slots: (0 slots) slave
   replicates cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Check for multiple slot owners...
[OK] No multiple owners found.




# disconnect node check
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster check my-release-redis-cluster-0-svc:6379 \
    --cluster-search-multiple-owners

my-release-redis-cluster-0-svc:6379 (3da89f16...) -> 0 keys | 0 slots | 0 slaves.
[OK] 0 keys in 1 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node my-release-redis-cluster-0-svc:6379)
M: 3da89f162fbe67e2bc76f5ae1a1301ecaee9931f my-release-redis-cluster-0-svc:6379
   slots: (0 slots) master
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[ERR] Not all 16384 slots are covered by nodes.

>>> Check for multiple slot owners...
[OK] No multiple owners found.

# error 를 리턴한다.




# master node check
$ redis-cli -h my-release-redis-cluster -a new1234 \
    --cluster check 10.42.0.175:6379 \
    --cluster-search-multiple-owners
    
    


    
```





## 2) cluster 명령들



```sh

$ redis-cli -h my-release-redis-cluster -a new1234 cluster help

 1) CLUSTER <subcommand> [<arg> [value] [opt] ...]. Subcommands are:
 2) ADDSLOTS <slot> [<slot> ...]
 3)     Assign slots to current node.
 4) ADDSLOTSRANGE <start slot> <end slot> [<start slot> <end slot> ...]
 5)     Assign slots which are between <start-slot> and <end-slot> to current node.
 6) BUMPEPOCH
 7)     Advance the cluster config epoch.
 8) COUNT-FAILURE-REPORTS <node-id>
 9)     Return number of failure reports for <node-id>.
10) COUNTKEYSINSLOT <slot>
11)     Return the number of keys in <slot>.
12) DELSLOTS <slot> [<slot> ...]
13)     Delete slots information from current node.
14) DELSLOTSRANGE <start slot> <end slot> [<start slot> <end slot> ...]
15)     Delete slots information which are between <start-slot> and <end-slot> from current node.
16) FAILOVER [FORCE|TAKEOVER]
17)     Promote current replica node to being a master.
18) FORGET <node-id>
19)     Remove a node from the cluster.
20) GETKEYSINSLOT <slot> <count>
21)     Return key names stored by current node in a slot.
22) FLUSHSLOTS
23)     Delete current node own slots information.
24) INFO
25)     Return information about the cluster.
26) KEYSLOT <key>
27)     Return the hash slot for <key>.
28) MEET <ip> <port> [<bus-port>]
29)     Connect nodes into a working cluster.
30) MYID
31)     Return the node id.
32) NODES
33)     Return cluster configuration seen by node. Output format:
34)     <id> <ip:port> <flags> <master> <pings> <pongs> <epoch> <link> <slot> ...
35) REPLICATE <node-id>
36)     Configure current node as replica to <node-id>.
37) RESET [HARD|SOFT]
38)     Reset current node (default: soft).
39) SET-CONFIG-EPOCH <epoch>
40)     Set config epoch of current node.
41) SETSLOT <slot> (IMPORTING <node-id>|MIGRATING <node-id>|STABLE|NODE <node-id>)
42)     Set slot state.
43) REPLICAS <node-id>
44)     Return <node-id> replicas.
45) SAVECONFIG
46)     Force saving cluster configuration on disk.
47) SLOTS
48)     Return information about slots range mappings. Each range is made of:
49)     start, end, master and replicas IP addresses, ports and ids
50) SHARDS
51)     Return information about slot range mappings and the nodes associated with them.
52) LINKS
53)     Return information about all network links between this node and its peers.
54)     Output format is an array where each array element is a map containing attributes of a link
55) HELP
56)     Prints this help.


```











### COUNT-FAILURE-REPORTS

```sh

CLUSTER COUNT-FAILURE-REPORTS node-id


# 정상 node-id 를 확인해보자.
CLUSTER COUNT-FAILURE-REPORTS 9fa9dc6cab3398624df40713b4bc675b1322184c
(integer) 0

CLUSTER COUNT-FAILURE-REPORTS 77b47a2e6cd38a19ce7b71eaf142b0ef87de16b9
(integer) 0

CLUSTER COUNT-FAILURE-REPORTS ff84e2066b155907cf0981f9c37bbc2686d8c880
(integer) 0



# 실패 node-id 를 확인해보자.
CLUSTER COUNT-FAILURE-REPORTS 37ede9c99774fcdb6ad3b34f7d681061244fba93
(integer) 2


```









```sh
CLUSTER FAILOVER [FORCE | TAKEOVER]

```







### forget

scale down 시점등에서 불필요한 node를 cluster 에서 제거할때 사용한다.

gosship protocol 에 의해서 session 이 처리되지않기를바라면서 최대한 빨리 모든 노드에 forge 명령을 보내야 성공할 수 있다.

일반적으로 60초 안에 처리해야 한다.

```sh

# cluster 확인
$ redis-cli -h my-release-redis-cluster -c -a new1234

$ cluster nodes
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689391983000 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689391981000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 myself,master - 0 1689391982000 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689391983141 7 connected
217cf57cc8a39cac12dd9d614317ad55fe4c57e4 10.42.0.171:6379@16379 master,fail - 1689391538631 0 0 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689391984146 1 connected



$ echo $REDIS_PASSWORD



# 각 node 에서 아래 명령 수행한다.

$ redis-cli -h localhost -a $REDIS_PASSWORD CLUSTER NODES
  redis-cli -h localhost -a $REDIS_PASSWORD CLUSTER FORGET 217cf57cc8a39cac12dd9d614317ad55fe4c57e4
  redis-cli -h localhost -a $REDIS_PASSWORD CLUSTER NODES


12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689389927000 2 connected 5461-10922
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689389927876 1 connected
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 myself,master - 0 1689389926000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 master - 0 1689389925867 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 12a06ae7cd0ae7efd267814e28a83ff7824b8178 0 1689389926872 2 connected
217cf57cc8a39cac12dd9d614317ad55fe4c57e4 10.42.0.171:6379@16379 master,fail - 1689389479341 1689389476321 3 connected

OK

12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689389927000 2 connected 5461-10922
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689389927876 1 connected
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 myself,master - 0 1689389926000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 master - 0 1689389925867 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 12a06ae7cd0ae7efd267814e28a83ff7824b8178 0 1689389926872 2 connected



# info 확인
my-release-redis-cluster:6379> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:5           <----
cluster_size:3
cluster_current_epoch:7
cluster_my_epoch:3
cluster_stats_messages_ping_sent:184701
cluster_stats_messages_pong_sent:181568
cluster_stats_messages_meet_sent:1
cluster_stats_messages_fail_sent:4
cluster_stats_messages_auth-ack_sent:1
cluster_stats_messages_sent:366275
cluster_stats_messages_ping_received:181568

# 5개로 줄었다.


```



### meet

마스터 노드에 복제서버 추가하기

```sh
# cluster 확인
$ redis-cli -h my-release-redis-cluster -c -a new1234

$ cluster nodes
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689392642726 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689392643000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 myself,master - 0 1689392642000 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689392641721 7 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689392643733 1 connected


# pod 확인
$ krs get pod -o wide
NAME                            READY   STATUS    RESTARTS   AGE     IP            NODE        NOMINATED NODE   READINESS GATES
python-7d59455985-scgxb         2/2     Running   0          5d22h   10.42.0.83    bastion03   <none>           <none>
redis-client-69dcc9c76d-g6sms   1/1     Running   0          2d14h   10.42.0.133   bastion03   <none>           <none>
my-release-redis-cluster-3      1/1     Running   0          99m     10.42.0.168   bastion03   <none>           <none>
my-release-redis-cluster-0      1/1     Running   0          99m     10.42.0.169   bastion03   <none>           <none>
my-release-redis-cluster-1      1/1     Running   0          99m     10.42.0.170   bastion03   <none>           <none>
my-release-redis-cluster-4      1/1     Running   0          99m     10.42.0.172   bastion03   <none>           <none>
my-release-redis-cluster-5      1/1     Running   0          99m     10.42.0.173   bastion03   <none>           <none>
my-release-redis-cluster-2      0/1     Running   0          54m     10.42.0.174   bastion03   <none>           <none>


# meet 명령 수행
# 신규 node 에서 아래 명령 수행
$ redis-cli -h my-release-redis-cluster -a $REDIS_PASSWORD CLUSTER meet 10.42.0.174 6379
  
  
  
```





### replicas

현재 node를 <node-id> 의 replca 로 설정한다.

```sh
# cluster 확인
$ redis-cli -h my-release-redis-cluster -c -a new1234

$ cluster nodes
12a06ae7cd0ae7efd267814e28a83ff7824b8178 10.42.0.170:6379@16379 master - 0 1689391225000 2 connected 5461-10922
95acfe21a64250fa3359eb10e5f9cd23d38ec36c 10.42.0.168:6379@16379 master - 0 1689391224000 7 connected 10923-16383
a2e3366d2310533a6cab5181afa197c93a5904a3 10.42.0.169:6379@16379 myself,master - 0 1689391226000 1 connected 0-5460
0da823d72181e684dfaf1b0c9cc9b58be96675ce 10.42.0.173:6379@16379 slave 95acfe21a64250fa3359eb10e5f9cd23d38ec36c 0 1689391226830 7 connected
217cf57cc8a39cac12dd9d614317ad55fe4c57e4 10.42.0.171:6379@16379 master,fail - 1689390078333 0 0 connected
b109ed74a0e639d34067d1bf78a860931d0ee941 10.42.0.172:6379@16379 slave a2e3366d2310533a6cab5181afa197c93a5904a3 0 1689391224817 1 connected


# 신규 node 에서 아래 명령 수행
$ redis-cli -h localhost -c -a $REDIS_PASSWORD CLUSTER REPLICATE 12a06ae7cd0ae7efd267814e28a83ff7824b8178
  


```







### addslots

해시슬롯을 할당한다.  Cluster 구성의 node 수정하는데 유용하다.

```sh

$ redis-cli -h my-release-redis-cluster-0-svc -a new1234 \
    cluster addslots 5000

(error) ERR Slot 5000 is already busy


REPLICATE

$ redis-cli  -a new1234 \
    cluster replicate 63d3744de13f2898137d4b90c55cc3639cfebe49



```







### slots

cluster slots map 의 상세정보를 제공한다.

이 명령은 클러스터 해시 슬롯을 실제 노드 네트워크 정보와 연결하는 맵을 검색(또는 리디렉션 수신 시 업데이트)하기 위해 Redis 클러스터 클라이언트 라이브러리 구현에서 사용하기에 적합하다.

```sh

$ cluster slots
1) 1) (integer) 0
   2) (integer) 5460
   3) 1) "10.42.0.172"
      2) (integer) 6379
      3) "b109ed74a0e639d34067d1bf78a860931d0ee941"
      4) (empty array)
   4) 1) "10.42.0.175"
      2) (integer) 6379
      3) "ac04246dfccf850cbdf78565a52f131b1160233a"
      4) (empty array)
2) 1) (integer) 5461
   2) (integer) 10922
   3) 1) "10.42.0.170"
      2) (integer) 6379
      3) "12a06ae7cd0ae7efd267814e28a83ff7824b8178"
      4) (empty array)
   4) 1) "10.42.0.174"
      2) (integer) 6379
      3) "da5aaba2df35da46204ff96eb4be853ccd507606"
      4) (empty array)
3) 1) (integer) 10923
   2) (integer) 16383
   3) 1) "10.42.0.168"
      2) (integer) 6379
      3) "95acfe21a64250fa3359eb10e5f9cd23d38ec36c"
      4) (empty array)
   4) 1) "10.42.0.173"
      2) (integer) 6379
      3) "0da823d72181e684dfaf1b0c9cc9b58be96675ce"
      4) (empty array)



10.42.0.172:6379> get a
-> Redirected to slot [15495] located at 10.42.0.168:6379
"1"
10.42.0.168:6379>
10.42.0.168:6379>
10.42.0.168:6379> get b
-> Redirected to slot [3300] located at 10.42.0.172:6379
"2"
10.42.0.172:6379>
10.42.0.172:6379>
10.42.0.172:6379> get c
-> Redirected to slot [7365] located at 10.42.0.170:6379
"3"

```







### GETKEYSINSLOT 

hash slot 에 저장된 key 의 배열을 리턴한다.

```sh
# CLUSTER GETKEYSINSLOT slot count

# 샘플 
# $ CLUSTER GETKEYSINSLOT 7000 3
# 1) "key_39015"
# 2) "key_89793"
# 3) "key_92937"




# 사전작업

10.42.0.172:6379> get a
-> Redirected to slot [15495] located at 10.42.0.168:6379
"1"
10.42.0.168:6379> get b
-> Redirected to slot [3300] located at 10.42.0.172:6379
"2"
10.42.0.172:6379> get c
-> Redirected to slot [7365] located at 10.42.0.170:6379
"3"
10.42.0.170:6379> get d
-> Redirected to slot [11298] located at 10.42.0.168:6379
"4"
10.42.0.168:6379> get e
(nil)
10.42.0.168:6379> set d d
OK
10.42.0.168:6379> set f f
-> Redirected to slot [3168] located at 10.42.0.172:6379
OK
10.42.0.172:6379> set g g
-> Redirected to slot [7233] located at 10.42.0.170:6379
OK






$ CLUSTER GETKEYSINSLOT 7365 10


```







## 3) persistence









# 4. Spring boot lettuce

Spring  boot lettuce 확인 



## 1) Connectionfactory

Master Node FailOver시 일반적인 ConnectionFactory에서는  connection 이 유지 되지 않는다.



### (1) common sample

```java

	@Bean(name = "reactiveRedisConnectionFactory")
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
		List<String> clusterNodes = Arrays.stream(host.split(","))
				  .map(String::trim)
				  .toList();
		
		for(String node: clusterNodes) {
			redisConfig.clusterNode(node, port);
		}
		
		redisConfig.setUsername(username);
		redisConfig.setPassword(password);
		
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                //.readFrom(ReadFrom.REPLICA_PREFERRED)
                .commandTimeout(Duration.ofSeconds(commandTimeDuration))
                .build();
		
		return new LettuceConnectionFactory(redisConfig, clientConfig);
	}

```





### (2) topologyRefresh sample

링크 : https://blog.leocat.kr/notes/2022/04/15/lettuce-config-for-redis-cluster-topology-refresh



topologyrefresh option 으로 재접속 시도

```java

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
		if(profiles.equals("local")) {
			log.debug("RedisRepositoryConfig - local - Redis Single");
	        LettuceClientConfiguration configuration = LettuceClientConfiguration.builder()
	                .readFrom(ReadFrom.MASTER)
	                .build();
	       	return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort),
	        			configuration);			
		}
		else {
			log.debug("RedisRepositoryConfig - Not local - Redis cluster");
			RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);  	
			redisClusterConfiguration.setPassword(redisPassword);

			ClusterTopologyRefreshOptions topologyRefreshOptins = ClusterTopologyRefreshOptions.builder()
					.dynamicRefreshSources(true) // 노드들을 discovery 활성화(기본적용)
//					.enablePeriodicRefresh(Duration.ofSeconds(5))// 5초마다 업데이트 (기본은 false)
					.enableAllAdaptiveRefreshTriggers() // 모든 이벤트 트리거에 대한 자동 갱신 활성화(기본은 특정 이벤트)
//					.adaptiveRefreshTriggersTimeout(Duration.ofSeconds(5)) // 30초마다 커넥션 갱신 (기본 1초)
					.build();			
			
			ClusterClientOptions clientOptions = ClusterClientOptions.builder()
					.topologyRefreshOptions(topologyRefreshOptins)
					.autoReconnect(true) // 자동 재접속 활성화
//					.timeoutOptions(TimeoutOptions.builder().fixedTimeout(Duration.ofSeconds(5)).build()) // 커넥션 타임아웃 5초
					.suspendReconnectOnProtocolFailure(true) // 연결중 발생하는 프로토콜 오류시 연결 일시 중단
//					.maxRedirects(3) // 기본은 5
					.build();
			
			LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
				.clientOptions(clientOptions)
				.build();						
			
			return new LettuceConnectionFactory(redisClusterConfiguration,lettuceClientConfiguration);
		}		
    }    
```









## 2) jib build



### (1) docker build & push

```sh


# 도커 데몬 빌드
$ mvn compile jib:dockerBuild


# 도커 데몬 빌드
$ mvn compile jib:dockerBuild  -Dimage=docker.io/ssongman/redis-sample:202307171134
$ mvn compile jib:dockerBuild  -Dimage=docker.io/ssongman/redis-sample:202307171149
$ mvn compile jib:dockerBuild  -Dimage=docker.io/ssongman/redis-sample:202307181207


$ docker images | grep redis-sample
ssongman/redis-sample                                     202307171134                                                                 a5e779f46a7
ssongman/redis-sample                                     latest                                                                       d94b0d544a2


$ docker push ssongman/redis-sample:latest
$ docker push ssongman/redis-sample:202307171134
$ docker push ssongman/redis-sample:202307171149
$ docker push ssongman/redis-sample:202307181207



```







### (2) deploy

```sh

$ krs create deploy redis-sample --image=ssongman/redis-sample:latest

# edit deploy
$ krs edit deploy redis-sample


```



### (3) test

```sh

$ krs exec -it deploy/redis-sample -- bash



# health
$ curl -X GET http://localhost:8082/health


# set person aaaa
$ curl -X POST http://localhost:8082/person \
  -H "Content-Type: application/json" \
  -d '{  
  "id": "aaaa",
  "name": "song",
  "age": 20,
  "createdAt": "2022-07-03T01:03:00"
}'

# set person bbbb
$ curl -X POST http://localhost:8082/person \
  -H "Content-Type: application/json" \
  -d '{  
  "id": "bbbb",
  "name": "Park",
  "age": 20,
  "createdAt": "2022-07-03T01:03:00"
}'


# get person aaaa
$ curl localhost:8082/person/aaaa

# get person bbbb
$ curl localhost:8082/person/bbbb



# 1초에 한번씩
$ while true; do curl localhost:8082/person/aaaa; echo; sleep 1;done

```





## 3) failover test

1초에한번씩 get명령을 수행하면서 fail over 처리를 해보자.



### (1) test1

get 하고 있는 node 와 무관한 master node 를 죽였을때...

정상이다.

가끔씩 아래 로그가 보인다.

```sh
2023-07-17 15:22:44.393  WARN 1 --- [ioEventLoop-4-1] i.l.core.protocol.ConnectionWatchdog     : Cannot reconnect to [10.42.0 │
│                                                                                                                               │
│ io.netty.channel.AbstractChannel$AnnotatedNoRouteToHostException: No route to host: /10.42.0.188:6379                         │
│ Caused by: java.net.NoRouteToHostException: No route to host                                                                  │
│     at java.base/sun.nio.ch.SocketChannelImpl.checkConnect(Native Method) ~[na:na]                                            │
│     at java.base/sun.nio.ch.SocketChannelImpl.finishConnect(Unknown Source) ~[na:na]                                          │
│     at io.netty.channel.socket.nio.NioSocketChannel.doFinishConnect(NioSocketChannel.java:337) ~[netty-transport-4.1.78.Final │
│     at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.finishConnect(AbstractNioChannel.java:334) ~[netty-transport │
│     at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:710) ~[netty-transport-4.1.78.Final.jar:4.1.78. │
│     at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:658) ~[netty-transport-4.1.78.Final.j │
│     at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:584) ~[netty-transport-4.1.78.Final.jar:4.1.78 │
│     at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:496) ~[netty-transport-4.1.78.Final.jar:4.1.78.Final]          │
│     at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997) ~[netty-common-4.1.78.Fin │
│     at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.78.Final.jar:4.1.78.Final │
│     at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.78.Final.jar: │
│     at java.base/java.lang.Thread.run(Unknown Source) ~[na:na] 
```

warn 이 보이는데 원인은???









### (2) test2

get 하고 있는 값이 존재하는 node 를 죽였을때

결과

지속적으로 reconnect 시도하면서 비정상적으로 작동한다.

```sh
                                                            │
│ 2023-07-17 15:27:15.215  INFO 1 --- [xecutorLoop-1-1] i.l.core.protocol.ConnectionWatchdog     : Reconnecting, last destinati │
│ 2023-07-17 15:27:18.281  WARN 1 --- [ioEventLoop-4-1] i.l.core.protocol.ConnectionWatchdog     : Cannot reconnect to [10.42.0 │
│                                                                           



```










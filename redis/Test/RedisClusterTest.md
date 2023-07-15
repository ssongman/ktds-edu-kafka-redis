



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





### (1) helm install

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



$ helm -n redis-system status my-release


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

7b8609f45e62d6060c3e34cd8bea661aeeed4e95 10.42.0.31:6379@16379 slave 89bd93950f1c63bce34d6639a056a6c59dd3bc6e 0 1686477766458 2 connected
da8eb2aaedc3906d4a3b7d56cf174a4b7c33f857 10.42.0.28:6379@16379 master - 0 1686477765454 3 connected 10923-16383
4a2e520ded34b4d21336089fe576c3c097eb97ea 10.42.0.27:6379@16379 myself,master - 0 1686477763000 1 connected 0-5460
15e403bacd76ad0cff575a8e86d24f58e20183f5 10.42.0.30:6379@16379 slave 4a2e520ded34b4d21336089fe576c3c097eb97ea 0 1686477764000 1 connected
89bd93950f1c63bce34d6639a056a6c59dd3bc6e 10.42.0.26:6379@16379 master - 0 1686477765000 2 connected 5461-10922
be0633d61c1da6271ed43f404f77b55792150765 10.42.0.29:6379@16379 slave da8eb2aaedc3906d4a3b7d56cf174a4b7c33f857 0 1686477764450 3 connected

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
cluster_stats_messages_ping_sent:236
cluster_stats_messages_pong_sent:233
cluster_stats_messages_sent:469
cluster_stats_messages_ping_received:228
cluster_stats_messages_pong_received:236
cluster_stats_messages_meet_received:5
cluster_stats_messages_received:469
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
    

# down 발생시점에서 약 20초정도 이후 정상작동한다.

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

















# 3. Cluster 명령들



참고링크 : 

https://redis.io/commands/cluster-addslots/







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

```sh


  rebalance      host:port
                 --cluster-weight <node1=w1...nodeN=wN>
                 --cluster-use-empty-masters
                 --cluster-timeout <arg>
                 --cluster-simulate
                 --cluster-pipeline <arg>
                 --cluster-threshold <arg>
                 --cluster-replace
                 
                 

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





### forget

```sh


$ redis-cli -h my-release-redis-cluster -a new1234 \
    cluster nodes

63d3744de13f2898137d4b90c55cc3639cfebe49 :0@0 master,fail,noaddr - 1689084949639 1689084947000 1 disconnected
cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379@16379 master - 0 1689089855416 3 connected 10923-16383
5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379@16379 slave fbdec1c82805e36da85530f0451a7ebcc63ac458 0 1689089853403 2 connected
8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379@16379 master - 0 1689089854000 7 connected 0-5460
fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379@16379 master - 0 1689089854408 2 connected 5461-10922
1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379@16379 myself,slave cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 0 1689089854000 3 connected


# 다운된 노드를 클러스터에서 제거한다.
my-release-redis-cluster:6379> cluster forget 63d3744de13f2898137d4b90c55cc3639cfebe49
OK

# node 확인
my-release-redis-cluster:6379> cluster nodes
fbdec1c82805e36da85530f0451a7ebcc63ac458 my-release-redis-cluster-1-svc:6379@16379 master - 0 1689089908000 2 connected 5461-10922
cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 my-release-redis-cluster-2-svc:6379@16379 myself,master - 0 1689089906000 3 connected 10923-16383
8fd49b4fb17c743a7f666e6a30b659fa39ecf73c my-release-redis-cluster-4-svc:6379@16379 master - 0 1689089908075 7 connected 0-5460
5953f810b2bf73c283c393c4ee9f725775759af3 my-release-redis-cluster-5-svc:6379@16379 slave fbdec1c82805e36da85530f0451a7ebcc63ac458 0 1689089907070 2 connected
1de28c8c3436e9b8f42a6a80382fcdc4903a97e3 my-release-redis-cluster-3-svc:6379@16379 slave cd6820769a5b3d75a1dd5e9739eb264fb6f60ab1 0 1689089909080 3 connected


# info 확인
my-release-redis-cluster:6379> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:5
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

클러스터로 묶기

```sh
$ redis-cli -h my-release-redis-cluster -a new1234 \
    cluster meet my-release-redis-cluster-0-svc 6379




REPLICATE

$ redis-cli  -a new1234 \
    cluster replicate 63d3744de13f2898137d4b90c55cc3639cfebe49



```







### addslots

```sh

$ redis-cli -h my-release-redis-cluster-0-svc -a new1234 \
    cluster addslots 5000

(error) ERR Slot 5000 is already busy


REPLICATE

$ redis-cli  -a new1234 \
    cluster replicate 63d3744de13f2898137d4b90c55cc3639cfebe49



```




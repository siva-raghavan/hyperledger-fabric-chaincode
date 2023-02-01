# hyperledger-fabric-chaincode
smartcode to deploy on hyperledger fabric blockchain


```git
cd ~/fabric-samples/test-network
```

#### bringing up/down the test network (optionally with couchdb for history)

```bash
./network.sh down
./network.sh up
./network.sh up -s couchdb
```

#### check network status in docker

```docker
docker ps
docker logs <container_name>
docker inspect <container_name>
docker exec -it <container_name> - bash
```

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

> 
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${FPWD}/organizations/peerOrganizations/org1.example.com/tlsca/tlsca.org1.example.com-cert.pem
export CORE_PEER_MSPCONFIGPATH=${FPWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051
export PATH=${FPWD}/../bin:$PATH
export FABRIC_CFG_PATH=${FPWD}/../config
export CORE_PEER_ADDRESS=127.0.0.1:7051

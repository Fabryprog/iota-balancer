# IOTA BALANCER

IOTA Node Balancer

## Feature

* load monitor alerting
* proxy balancing using performance

## Docker Compose

### IOTA Node

```
version: "2"

services:

  monitor:
    image: fabryprog/vps-monitor:latest
    restart: unless-stopped 
    volumes:
      - /etc/hostname:/etc/hostname:ro
      - $PWD/config.json:/opt/monitor/config.json

  iri:
    image: iotaledger/iri:latest
    hostname: iri
    restart: unless-stopped
    volumes:
      - ./volumes/iri/iri.ini:/iri/conf/iri.ini:ro
      - ./volumes/iri/ixi:/iri/ixi:rw
      - ./volumes/iri/data:/iri/data:rw
      - /etc/localtime:/etc/localtime:ro
    environment:
      - JAVA_MAX_MEMORY=4096m
      - JAVA_MIN_MEMORY=256m
      - DOCKER_IRI_MONITORING_API_PORT_ENABLE=1
    expose:
      - "5556"
      - "14266"
    ports:
      - "14600:14600/udp"
      - "15600:15600/tcp"
      - "14265:14265" # comment when nginx is active
    command: ["-c", "/iri/conf/iri.ini"]
```


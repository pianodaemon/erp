### Elements which compose this project

Some of the elements of this project may be deemed as obsoletes but there is currently an active quest for innovative ways to resurrect them totally or partially.

| Artifact | Brief description |
| ------ | ------ |
| DOS/cfdiengine | microservice in charge of issuing cfdi documents |
| DOS/sales-api | microservice that administers the sale flow |
| DOS/sso | single sign on |
| DOS/reports/builders | reports formats library |
| DOS/reports/cli | command line to render reports from operation |
| DOS/reports/godinez | POM project, usable as a parent for other modules |
| DOS/legacy/sales-grpc-jcli | java client part of the sales api |
| DOS/legacy/bbgum-jcli | java client part of bbgum tcp protocol |
| DOS/legacy/bbgum-impt | implementation bbgum client for r1 | 
| DOS/legacy/front-end | web-ui with the overall of user interfaces that has agnux erp |
| DOS/legacy/r1           | POM project, usable as a parent for other modules |


#### Environment variables per implementation

```sh
# determines where the implementation was made
export ERP_ROOT="/immortalcrab"

# determines how we have named this implementation
export ERP_CUSTOMER="johndove"

# staging area for CI
export STAGE="$ERP_ROOT/stage"

# dbms settings for the legacy stuff
export MS_DBMS_DB="erp"
export MS_DBMS_USER="postgres"
export MS_DBMS_HOST="127.0.0.1"
export MS_DBMS_PASS="postgres"
export MS_DBMS_PORT=5432

# grpc servers for the satelite modules as microservices
export SALES_GRPC_HOST="127.0.0.1"
export SALES_GRPC_PORT="10090"
export COBRANZA_GRPC_HOST="127.0.0.1"
export COBRANZA_GRPC_PORT="10110"

# cfdiengine server
export CFDIENGINE_HOST="127.0.0.1"
export CFDIENGINE_PORT="10080"

# inclusion of a few management scripts into the path
export PATH="$ERP_ROOT/bin:$PATH"

# it directly takes you toward the implementation
cd $ERP_ROOT

```


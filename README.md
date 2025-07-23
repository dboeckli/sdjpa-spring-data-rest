# Spring Data REST - Example Project

This project demonstrates the implementation of a Spring Data REST application for managing beer data. It showcases the power and simplicity of Spring Data REST in creating RESTful APIs with minimal boilerplate code. The application provides CRUD operations for beer entities, supports pagination, sorting, and custom query methods. It includes both a RESTful API and a web interface for user interaction.

Key features:
- RESTful API for beer data management
- Custom query methods for searching beers by name, style, and UPC
- Web interface for viewing and navigating beer data
- Integration with MySQL database (with H2 option for development)
- Flyway for database migration management
- Docker and Kubernetes support for easy deployment
- Swagger/OpenAPI documentation

## Swagger/Openapi Url

- local: http://localhost:8080/swagger-ui/index.html, http://localhost:8080/v3/api-docs
- k8s: http://localhost:30080/swagger-ui/index.html, http://localhost:30080/v3/api-docs

## Web Interface

This application includes a web interface that allows users to interact with the beer data through a browser. The web interface provides the following features:

- View a paginated list of beers
- Navigate through pages of beer listings
- View details of individual beers

To access the web interface, start the application and navigate to:

- local: http://localhost:8080/web/beers
- k8s: http://localhost:30080/web/beers


## Flyway

To enable Flyway in the MySQL profile, override the following properties when starting the application:
- `spring.flyway.enabled = true`
- `spring.docker.compose.file = compose-mysql.yaml`

This profile starts MySQL on port 3306 using the Docker Compose file `compose-mysql-.yaml`.

## Docker

Docker Compose file initially use the startup script located in `src/scripts`. These scripts create the database and users.

## Kubernetes

### Generate Config Map for mysql init script

When updating 'src/scripts/init-mysql-mysql.sql', apply the changes to the Kubernetes ConfigMap:
```bash
kubectl create configmap mysql-init-script --from-file=init.sql=src/scripts/init-mysql.sql --dry-run=client -o yaml | Out-File -Encoding utf8 k8s/mysql-init-script-configmap.yaml
```

### Deployment with Kubernetes

Deployment goes into the default namespace.

To deploy all resources:
```bash
kubectl apply -f target/k8s/
```

To remove all resources:
```bash
kubectl delete -f target/k8s/
```

Check
```bash
kubectl get deployments -o wide
kubectl get pods -o wide
```

You can use the actuator rest call to verify via port 30080

### Deployment with Helm

Be aware that we are using a different namespace here (not default).

Go to the directory where the tgz file has been created after 'mvn install'
```powershell
cd target/helm/repo
```

unpack
```powershell
$file = Get-ChildItem -Filter sdjpa-spring-data-rest-v*.tgz | Select-Object -First 1
tar -xvf $file.Name
```

install
```powershell
$APPLICATION_NAME = Get-ChildItem -Directory | Where-Object { $_.LastWriteTime -ge $file.LastWriteTime } | Select-Object -ExpandProperty Name
helm upgrade --install $APPLICATION_NAME ./$APPLICATION_NAME --namespace sdjpa-spring-data-rest --create-namespace --wait --timeout 5m --debug
```

show logs
```powershell
kubectl get pods -n sdjpa-spring-data-rest
```

replace $POD with pods from the command above
```powershell
kubectl logs $POD -n sdjpa-spring-data-rest --all-containers
```

Show Endpoints
```powershell
kubectl get endpoints -n sdjpa-spring-data-rest
```

test
```powershell
helm test $APPLICATION_NAME --namespace sdjpa-spring-data-rest --logs
```

status
```powershell
helm status $APPLICATION_NAME --namespace sdjpa-spring-data-rest
```

uninstall
```powershell
helm uninstall $APPLICATION_NAME  --namespace sdjpa-spring-data-rest
```

delete all
```powershell
kubectl delete all --all -n sdjpa-spring-data-rest
```

create busybox sidecar
```powershell
kubectl run busybox-test --rm -it --image=busybox:1.36 --namespace=sdjpa-spring-data-rest --command -- sh
```

You can use the actuator rest call to verify via port 30080

## Running the Application
1. Choose between h2 or mysql for database schema management. (you can use one of the preconfigured intellij runners)
2. Start the application with the appropriate profile and properties.
3. The application will use Docker Compose to start MySQL and apply the database schema changes.
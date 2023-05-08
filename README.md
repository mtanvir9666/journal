# Getting Started



## Step 0: Minikube setup
```
    minikube stop
    minikube start --memory=10000 --cpus=4
    minikube stop && minikube delete             // remove minikube
    minikube start --v=10 --vm-driver=hyperkit   // start a fresh one
    minikube start --memory=10000 --cpus=4 --v=10 --vm-driver=hyperkit
    minikube start --kubernetes-version=v1.18.3
```
## Step 0: Helm Commands
```
# Pipeline Using Github Action

helm repo list
helm repo add <reponame> <URL>
helm install myRealse <repoName/chartName>

helm list (show all releases
helm show values <repoName/chartName>
```
## Step 0: Docker Commands
```
# Build image
doker build --rm -t journal:latest .       [ --rm = remove intermediate images. -t tag]

# Run Container
docker  run --rm --name myjournal -p 8880:80 journal:latest <command>    
[--rm =remove container after exit, -p = <external port: <internal port>] 
[-d = backgroud daemon, -it = attach TTY --memory 200m, --memory-swap 1G, --cpu-shares 1024]

# Publish docker image
docker tag source_image:source_tag TAG target_image:target_tag
docker login
docker push image:tag

docker images ---- list images in your system

# Cleanup
docker rmi journal:latest
docker rmi <imageid>

docker system prune [ removes stopped/untagged/unused image layers]
```
 
## Step 1: Build Journal application
	- use Java SpringBoot initializer using JDK 19
## Step 2: Push code into Github
	- Run "git init" at your repo and commit the changes in your local machine
	- Setup SSH credential 
		- https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent
	- Create a repo using the Github UI
	- Run the command to point to the remote repo (existing project) in local machine
		- ?
	- Publish the changes to Github
		- g push -u origin main
## Step 3: Push Docker image
	- docker build --build-arg JAR_FILE=build/libs/\*.jar -t mtanvir96/journal .
	- docker push mtanvir96/journal[:tagname]         
		- mtanvir96 is the registry name and 'journal' is the image name. And no tag specified mean 'latest' tag
		- by default pushed to docker hub
		- docker push mtanvir96/journal[:tagname] -registry <registry URL>    [ if you want to specify a separate registry ]
## Step 4: Helm chart and Deploy service
	- Create a chart directory
	- Run "helm create journal" inside the chart directory
	- Update values.yaml to have
```
        image:
            repository: mtanvir96/journal
            tag: "latest"
```
	-      Update charts.yaml to have
```
        appVersion: "1.0.0"
```
	- cd  /chart/journal
	- Comment out the following line from the deplyment.yaml to bring up the pod
```
          # livenessProbe:
          #   httpGet:
          #     path: /
          #     port: http
          # readinessProbe:
          #   httpGet:
          #     path: /
          #     port: http
```
	- Install chart
```
helm install journal --debug --dry-run .     [release name = journal]
helm install journal --debug  .     
helm upgrade journal .            [if needed to update the release]

helm delete journal
```

	- Port forward to access the service
```
k port-forward pod/journal-685bd4b75-ntsqx 8080:8080   (local:remote ports)  
[Make sure you app is running on port 8080. Check deployment.yaml (containerPort = 8080)]

k port-forward service/journal 8080:80   (local:remote ports)  
[Make sure you k8s service object is configured with port 80. Check service.yaml (port = 80)]
[localPort(8080) ---------- Service port(80)------------->app port(8080)]
```

## Step 5: Pipeline (Using Github Action)
	-    Build
	-    Test
	-    Docker image build and publish

## Step 6: Setup Ingress  (using minikube)
	- Modify deployment. yaml  as follows:
```
containerPort: 8080
```
	- Modify ingress.yaml as follows
```
            apiVersion: networking.k8s.io/v1   <----------------change
             ...
             ...
		  rules:
		    {{- range .Values.ingress.hosts }}
		    - host: {{ .host | quote }}
		      http:
		        paths:
		          {{- range .paths }}
		          - path: {{ .path }}
		            pathType: Prefix
		            backend:
		              service:
		                name: {{ $fullName }}
		                port:
		                  number: {{ $svcPort }}

		              # serviceName: {{ $fullName }}
		              # servicePort: {{ $svcPort }}
		          {{- end }}
		    {{- end }}
```
	- Modify values.yaml as follows:
```
ingress:
  enabled: true
...
hosts:
 - host:
   - path:
     backend: 
       serviceName: journal
```
	- Start the default Ingress Controller in minikube
```
minikube addons enable ingress
```
	- Redeploy the service using helm command
```
helm upgrade journal .
or
helm install journal . [ from charts/journal directory ]
```
	- Add the following in /etc/hosts  (map domain-name with ip address)
```
# Get the ip address from "k get ingress journal"
192.168.64.3    chart-example.local   
192.168.64.3
  
```

	- Test with
```
http://chart-example.local/journal
```

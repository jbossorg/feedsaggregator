# Kubernetes deployment

This example shows how to build and deploy feeds aggregator in Kubernetes.
The Tekton pipelines is used to build images. The images could be build manually then the Tekton part can be skipped.

## Prerequisites

Have running k8s cluster e.g. minikube

### Minikube

Install Minikube: https://kubernetes.io/docs/tasks/tools/install-minikube/

Mac:
```
brew install minikube
```

Start minikube
```
minikube start
```

Add registry helpers

```
minikube addons enable registry && minikube addons enable registry-aliases
```

### Tekton Pipelines

Install [Tekton](https://github.com/tektoncd/pipeline/blob/master/docs/install.md)

```
kubectl apply --filename https://storage.googleapis.com/tekton-releases/pipeline/latest/release.yaml
```

Optionally install [Tekton CLI](https://github.com/tektoncd/cli) or

Dashboard [Tekton Dashboard](https://github.com/tektoncd/dashboard/blob/master/docs/install.md#installing-tekton-dashboard-on-kubernetes)

```
kubectl apply --filename https://storage.googleapis.com/tekton-releases/dashboard/latest/tekton-dashboard-release.yaml
kubectl get pods --namespace tekton-pipelines --watch
# Wait for `Running`, then CTRL+C
```

#### Tekton official tasks

```
# git-clone and maven tasks
kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/master/task/git-clone/0.2/git-clone.yaml
kubectl apply -f https://raw.githubusercontent.com/tektoncd/catalog/master/task/maven/0.2/maven.yaml
```

## Build images

Create build pipeline:
```
kubectl apply -f deployment/k8s/pipeline/build-pipeline.yaml
```

Run build pipeline:
```
kubectl apply -f deployment/k8s/pipeline/build-pipeline-run.yaml
```

Wait till pods completed `kubectl get pods --watch` resp. watch the task by `kubectl logs feedsagg-build-pipeline-run-build-push-image-rn86n-pod-qwp5l step-mvn-goals`.

Note. To rerun it's needed to delete the latest run by `kubectl delete pipelineruns.tekton.dev feedsagg-build-pipeline-run`.

## Deploy

1. Mongo
```
kubectl apply -f deployment/k8s/deployment/mongo.yaml
```

2. REST API
```
kubectl apply -f deployment/k8s/deployment/api-mongo.yaml
```
and tunnel the service
```
minikube service api-mongo
```

3. Feeds2Mongo Cron Job

Upload `config/feeds-config.yaml` as configmap

```
kubectl create configmap feeds2mongo-feedsconfig --from-file=config/feeds-config.yaml
```

```
kubectl apply -f deployment/k8s/deployment/feeds2mongo-cronjob.yaml
```
and watch jobs
```
kubectl get jobs --watch
```

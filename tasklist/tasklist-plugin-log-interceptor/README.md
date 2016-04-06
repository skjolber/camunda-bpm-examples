#Tasklist Plugin Log-Interceptor

This example demonstrates how to add custom logging to the REST API as a Tasklist plugin. 

It compliments the [already present][2] logging of events in the process engine by adding read access logging as mandated by certain regulatory requirements.  

**Note**: This example is without an user-interface and thus does not demonstrate the full potential of tasklist plugins. Visit the [consulting snippets][1] for more complete examples.

## Show me the important parts!

The plugin enables a provider 

```java
@Provider
@ServerInterceptor
public class LogInterceptorProvider implements PreProcessInterceptor, AcceptedByMethod, PostProcessInterceptor {

}
```

which is invoked with `preProcess(..)` and `postProcess(..)` methods for request and response. The `postProcess(..)` method lets you work on the response DTOs rather than the raw byte stream.

## How does it work?
The Provider class is addes as a resource in the plugin. 

## How to use it?

1. Checkout the project with Git
2. Import the project into your IDE
3. Inspect the sources
4. Build and deploy to your Camunda server under /webapps/camunda/WEB-INF/lib 
5. (Optionally) Touch /webapps/camunda/WEB-INF/web.xml for redeployment
6. Open Tasklist and interact with some tasks
7. Observe log output 

## Tweaking the example

As supported by Resteasy, various `@Provider`-annotated classes could be added as customization points. 

[1]: https://github.com/camunda/camunda-consulting/tree/master/snippets
[2]: https://docs.camunda.org/manual/7.4/user-guide/process-engine/history/

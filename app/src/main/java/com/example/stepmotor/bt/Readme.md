```kotlin
decoder.run()
decoder.addCmd("pong") {
}
```


```mermaid
flowchart TD
    run --> decodeScope
    run --> commandDecoder
    run --> cliDecoder


subgraph decodeScope
    channelIn -- полная строка --> channelRoute
end

subgraph commandDecoder
    channelRoute --> channelOutCommand
end

subgraph cliDecoder
    channelOutCommand --> parse --> r("Выполенение команды")
end

   

    
```


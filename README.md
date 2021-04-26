# ntc-jthumbnail
ntc-jthumbnail is a module java create thumbnail for type file: office, pdf, image, html, txt.

## Usage
```java
public static void main(String[] args) {
    ConsumerRBQueue cq = new ConsumerRBQueue();
    cq.add(new ThumbnailOfficeWorker());
    cq.add(new ThumbnailOfficeWorker());
    cq.add(new ThumbnailOfficeWorker());
    cq.add(new ThumbnailOfficeWorker());

    cq.add(new ConvertPDFWorker());
    cq.add(new ConvertPDFWorker());

    cq.start();
}
```

## License
This code is under the [Apache License v2](https://www.apache.org/licenses/LICENSE-2.0).  

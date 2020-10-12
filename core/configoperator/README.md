# Configuration operator

Reads the feeds configuration file and trigger processing jobs for each feed.

## Job Configuration

1. `configUrl` - URL of configuration. e.g. file:///app/config/feeds-config.yaml
2. `jobName` - TODO

## Configuration file schema

```yaml
- group1:
 - code: feedcode1
   title: Feed title (Optional)
   author: Name of Author used if the original Feed author not present (Optional)
   url: http://example.com/atom.xml
 - code: feedcode2
   url: http://example.com/atom2.xml
- group2:
 - code: feedcode3
   url: http://example.com/atom3.xml
 - code: feedcode4
   url: http://example.com/atom4.xml
```

# sftp
SFTP implementation for java

**This library is currently a work in progress.**

The initial implementation will be a wrapper around JSch that provides additional concurrency through the use of multiple channels.

Once the initial implementation is complete I am planning on evaluating the speed provided by simply wrapping JSch. If this implementation is significantly slower than command line SFTP I am interested in writing my own implemenation of an SFTP client.

In the future support may be added for different protocols: SCP, shell commands, etc.

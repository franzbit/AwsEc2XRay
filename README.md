
GOAL: run this SpringBoot app on an AWS EC2 instance and check if X-Ray daemon sends segments to X-Ray-API and finally to X-Ray Console


starte EC2-Instanz mit  Instance-Profil CodeDeployDemo-EC2-Instance-Profile
und SSH-KeyPair von User X


kopiere SpringApp in S3-Bucket wenn scp nicht geht
wget https://blabla.s3-us-west-2.amazonaws.com/EC2_X-Ray.zip


installiere JDK zusätzlich, ujm SpringApp zu kompilieren ( installiertes openjdk enthält nur JRE )


kopiere X-Ray aus S3-Bucket:
wget https://s3.dualstack.us-west-2.amazonaws.com/aws-xray-assets.us-west-2/xray-daemon/aws-xray-daemon-3.x.rpm
install X-Ray daemon on EC2 instance
https://docs.aws.amazon.com/de_de/xray/latest/devguide/xray-daemon.html
https://docs.aws.amazon.com/de_de/xray/latest/devguide/xray-daemon-ec2.html
Installiere X-Ray "Überprüfen der Signatur des Daemon-Archivs" - https://docs.aws.amazon.com/de_de/xray/latest/devguide/xray-daemon.html
starte X-Ray
./xray -o -n us-east-2


füge ~/.aws/credentials mit access&secret key hinzu


Füge Instance-Profil CodeDeployDemo-EC2-Instance-Profile
Richtlinie "AWSXRayDaemonWriteAccess" hinzu - u.a. "xray:PutTraceSegments"

starte SpringApp


check Aufrufnachverfolgung - dada
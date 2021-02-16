import boto3
import os
from custom.profile import env_property


_BOTO_KWARGS = {
    "aws_access_key_id": env_property("AWS_ACCESS_KEY_ID"),
    "aws_secret_access_key": env_property("AWS_SECRET_ACCESS_KEY"),
    "region_name": env_property("AWS_REGION"),
}


class SthreeOps(object):
   """Encapsulation that stands for s3 operations"""

    def __init__(self, **kwargs):
        self.session = boto3.Session(**kwargs)

    def __del__(self):
        pass

    @staticmethod
    def _bucketize(session, target_bucket, fname):
        """Having a session it uploads the file into the s3 bucket"""
        if not os.path.isfile(fname):
            raise Exception("unable to locate file {}".format(fname))

        key_fname = os.path.basename(fname)

        client = session.client("s3")

        try:
            client.upload_file(fname, target_bucket, key_fname)
        except ClientError as e:
            emsg = "file {} could not be placed at {} : ({})"
            raise Exception(emsg.format(fname, target_bucket, e))

    @staticmethod
    def _retrieve_str(session, target_bucket, item_name):
        """Having a session it retrieves an utf-8 string"""
        client = session.client("s3")
        try:
            obj = client.get_object(Bucket=target_bucket, Key=item_name)
            return obj['Body'].read().decode('utf-8')
        except ClientError as e:
            emsg = "file {} could not be retrieved from {} : ({})"
            raise Exception(emsg.format(item_name, target_bucket, e))

    @classmethod
    def placement(cls, target_bucket, fname):
        """It places a regular file into a s3 bucket"""
        ic = cls(**_BOTO_KWARGS)
        cls._bucketize(ic.session, target_bucket, fname)

    @classmethod
    def pull_text(cls, target_bucket, fname):
        """It retrieves an utf-8 string"""
        ic = cls(**_BOTO_KWARGS)
        cls._retrieve_str(ic.session, target_bucket, fname)

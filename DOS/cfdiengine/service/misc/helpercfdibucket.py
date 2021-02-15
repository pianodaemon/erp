import boto3
from custom.profile import env_property

_boto_kwargs = {
    "aws_access_key_id": env_property("AWS_ACCESS_KEY_ID"),
    "aws_secret_access_key": env_property("AWS_SECRET_ACCESS_KEY"),
    "region_name": env_property("AWS_REGION"),
}

class HelperCfdiBucket(object):
    """
    """

    @staticmethod
    def _placement(target_bucket):
        sthree_res = boto3.resource('s3')

    @classmethod
    def bucketize(cls, fpath, target_bucket):
        client = boto3.Session(**boto_kwargs).client("s3")

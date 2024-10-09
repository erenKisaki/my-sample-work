import time
from botocore.exceptions import NoCredentialsError, PartialCredentialsError

def assume_role(account_id, role_name, retry_attempts=3, delay=2):
    """
    Assume a role in a different AWS account with retries and return a session.
    """
    role_arn = f"arn:aws:iam::{account_id}:role/{role_name}"
    
    for attempt in range(retry_attempts):
        try:
            response = sts_client.assume_role(
                RoleArn=role_arn,
                RoleSessionName='CrossAccountDynamoDBSession'
            )
            credentials = response['Credentials']
            return boto3.Session(
                aws_access_key_id=credentials['AccessKeyId'],
                aws_secret_access_key=credentials['SecretAccessKey'],
                aws_session_token=credentials['SessionToken']
            )
        except (ClientError, NoCredentialsError, PartialCredentialsError) as e:
            logger.error(f"Failed to assume role {role_arn}: {e}")
            if attempt < retry_attempts - 1:
                logger.info(f"Retrying... (Attempt {attempt + 1})")
                time.sleep(delay)
            else:
                logger.error(f"Exceeded maximum retries for assuming role {role_arn}")
                raise

def get_envelope_data_from_cross_account(account_id, role_name, group_id, table_name):
    """
    Retrieve data from DynamoDB table in a different AWS account with error handling.
    """
    try:
        cross_account_session = assume_role(account_id, role_name)
        dynamodb = get_dynamodb_client(cross_account_session)
        table = dynamodb.Table(table_name)
        
        response = table.get_item(Key={'group_id': group_id})
        
        if 'Item' in response:
            return response['Item']
        else:
            logger.warning(f"No data found for group_id: {group_id}")
            return None
    
    except ClientError as e:
        logger.error(f"DynamoDB ClientError: {e.response['Error']['Message']}")
        raise
    except Exception as e:
        logger.error(f"Unexpected error occurred: {str(e)}")
        raise


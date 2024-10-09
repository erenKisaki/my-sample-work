import boto3
from botocore.exceptions import ClientError
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

# STS client to assume role
sts_client = boto3.client('sts')

def assume_role(account_id, role_name):
    """
    Assume a role in a different AWS account and return a session with temporary credentials.
    """
    try:
        role_arn = f"arn:aws:iam::{account_id}:role/{role_name}"
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
    except ClientError as e:
        logger.error(f"Error assuming role {role_arn}: {e}")
        raise

def get_dynamodb_client(cross_account_session):
    """
    Get a DynamoDB resource using the cross-account session.
    """
    return cross_account_session.resource('dynamodb')

def get_envelope_data_from_cross_account(account_id, role_name, group_id, table_name):
    """
    Retrieve data from a DynamoDB table in a different AWS account using cross-account access.
    """
    # Assume the cross-account role
    cross_account_session = assume_role(account_id, role_name)

    # Use the assumed session to create a DynamoDB resource
    dynamodb = get_dynamodb_client(cross_account_session)
    table = dynamodb.Table(table_name)

    try:
        logger.info(f"Retrieving envelope data for group_id: {group_id} from cross-account DynamoDB")
        response = table.get_item(
            Key={'group_id': group_id}
        )
        if 'Item' in response:
            return response['Item']  # Return the retrieved item
        else:
            logger.info(f"No data found for group_id: {group_id}")
            return None

    except ClientError as ex:
        logger.error(f"Error occurred while fetching data from DynamoDB: {ex}")
        raise

# Example usage
def send_invest_to_ckd(context, group_id, account_id, role_name, table_name):
    logger.info(f"Inside the send_invest_to_ckd method for group_id: {group_id}")

    # Fetch the envelope data from the cross-account DynamoDB table
    envelope_data = get_envelope_data_from_cross_account(account_id, role_name, group_id, table_name)

    if envelope_data:
        check_list = envelope_data.get('checks', [])

        if check_list:
            if envelope_data.get('is_tab3', False):
                send_invest_for_tab3(context, group_id, check_list, envelope_data)
            else:
                send_invest_for_tab1(context, group_id, check_list, envelope_data)
        else:
            logger.info("No checks retrieved from DynamoDB to send the invest")
    else:
        logger.info("No envelope data found for the given group_id")

def send_invest_for_tab3(context, group_id, check_list, envelope_data):
    # Your logic for handling Tab3
    pass

def send_invest_for_tab1(context, group_id, check_list, envelope_data):
    # Your logic for handling Tab1
    pass

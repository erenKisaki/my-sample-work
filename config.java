    def update_status(self, group_id):
        logger.info("In update_status method")

        try:
            # Token retrieval
            token_service = TokenUtils()
            token = token_service.get_okta_token()
            headers = {'Authorization': f'Bearer {token}', 'Content-Type': 'application/json'}

            # URL setup
            url = f"{self.wih_url}/oauth/groups/{group_id}/status"

            # Payload as per your doc screenshot
            payload = {
                "status": "STP INVEST PROCESSING",
                "comment": "Started STP Process",
                "precondition": "READYTOROUTE"
            }

            # API Call
            response = requests.put(url, json=payload, headers=headers)

            if response.status_code == 200:
                logger.info("WIH status update call was successful: %s", response.text)
                msg = "STP_STATUS_UPDATED"
                publish_sns_notification(msg)
                return response

            else:
                logger.info("WIH status update call was not successful: %s", response.text)
                msg = "STP_STATUS_UPDATE_FAILED"
                publish_sns_notification(msg)
                raise ProcessException(f"Failed Response: {response.text}")

        except Exception as e:
            error_message = (
                f"Error while updating status for group_id: {group_id} "
                f"Exception: {str(e)}"
            )
            raise ProcessException(error_message) from e

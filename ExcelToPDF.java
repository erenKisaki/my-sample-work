    def extract_trans_code(self, response):
        """
        Extract transCode from the JSON response.
        """
        try:
            data = response.json()
            trans_code = data.get("transCode")
            return trans_code
        except json.JSONDecodeError as e:
            logger.error("Error parsing JSON response: %s", str(e))
            raise Exception("Invalid JSON response") from e

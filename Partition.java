mocoServer.request(containsTexts(cardPaymentRequest.getMid(), CHASE_PE))
    .response(status(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            jsonContent(),
            text("Silent fail error msg"));

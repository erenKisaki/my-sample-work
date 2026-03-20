ResponseEntity
        .status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR)
        .build();

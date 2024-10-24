package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return driver response entity"
    request {
        method 'GET'
        url '/api/drivers/1'
        headers {
            header('Content-Type', 'application/vnd.fraud.v1+json')
        }
    }
    response {
        status 200
        body("""
        {
                "id": 1,
                "name": "Ivan1",
                "email": "ivan1@gmail.com",
                "phoneNumber": "+375291239871",
                "sex": "MALE",
                "car": {
                    "id": 1,
                    "color": "WHITE",
                    "brand": "AUDI",
                    "carNumber": "1234AB-1",
                    "deleted": false
                },
                "deleted": false
        }
                """
        )
    }
}
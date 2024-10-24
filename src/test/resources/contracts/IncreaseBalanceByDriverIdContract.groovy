//package contracts
//
//import org.springframework.cloud.contract.spec.Contract
//
//
//Contract.make {
//    description "Should return driver account response entity"
//    request {
//        method 'PUT'
//        url '/api/drivers/2/accounts/up'
//        headers {
//            header('Content-Type', 'application/vnd.fraud.v1+json')
//        }
//        body("""
//            {
//                "balance": 1000.5,
//                "currency": "BYN"
//            }
//        """
//        )
//    }
//    response {
//        status 200
//        body("""
//            {
//                "id" : 2,
//                "driver" : {
//                    "id": 2,
//                    "name": "Ivan2",
//                    "email": "ivan2@gmail.com",
//                    "phoneNumber": "+375291239872",
//                    "sex": "MALE",
//                    "car": {
//                        "id": 2,
//                        "color": "WHITE",
//                        "brand": "AUDI",
//                        "carNumber": "1234AB-2",
//                        "deleted": false
//                    },
//                    "deleted": false
//                },
//                "balance" : 1000.5,
//                "currency" : "BYN"
//            }
//        """
//        )
//    }
//}

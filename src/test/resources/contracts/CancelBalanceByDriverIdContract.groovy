//package contracts
//
//import org.springframework.cloud.contract.spec.Contract
//
//Contract.make {
//    description "Should return driver account response entity"
//    request {
//        method 'PUT'
//        url '/api/drivers/3/accounts/down'
//        headers {
//            header('Content-Type', 'application/vnd.fraud.v1+json')
//        }
//        body("""
//            {
//                "balance": 0,
//                "currency": "BYN"
//            }
//        """
//        )
//    }
//    response {
//        status 200
//        body("""
//            {
//                "id" : 3,
//                "driver" : {
//                    "id": 3,
//                    "name": "Ivan3",
//                    "email": "ivan3@gmail.com",
//                    "phoneNumber": "+375291239873",
//                    "sex": "MALE",
//                    "car": {
//                        "id": 3,
//                        "color": "WHITE",
//                        "brand": "AUDI",
//                        "carNumber": "1234AB-3",
//                        "deleted": false
//                    },
//                    "deleted": false
//                },
//                "balance" : 0,
//                "currency" : "BYN"
//            }
//        """
//        )
//    }
//}
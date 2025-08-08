// cmd Windows

// check method -> getPayment
curl -v -X GET "http://localhost:8080/api/payments/ac328a1a-1e60-4dd3-bee5-ed573d74c841"

// check method -> getPayments
curl -v -X GET "http://localhost:8080/api/payments/all"

//  check method -> delete
curl -v -X DELETE "http://localhost:8080/api/payments/223e4567-e80b-12d3-a456-426614174008"

//  check method -> updateNote
curl -v -X PATCH "http://localhost:8080/api/payments/223e4907-e80b-12d3-a456-426614174008/note" ^
-H "Content-Type: application/json" ^
-d "{\"note\":\"new noteTest\"}"

//  check method -> create
curl -v -X POST "http://localhost:8080/api/payments" ^
-H "Content-Type: application/json" ^
-d "{""inquiryRefId"":""a1b2c3d4-e5f6-7990-abcd-1234567890ab"",""amount"":122.50,""currency"":""USD"",""transactionRefId"":""b2c3d1e5-f678-90ab-cdef-1234567890ab"",""status"":""DECLINED"",""note"":""new note"",""createdAt"":""2025-08-01T10:15:30+03:00"",""updatedAt"":""2025-08-08T16:20:00+03:00""}"

//  check method -> update
curl -v -X PUT "http://localhost:8080/api/payments/ac328a1a-1e60-4dd3-bee5-ed573d74c841" ^
-H "Content-Type: application/json" ^
-d "{""inquiryRefId"":""a1b2c3d4-e5f6-7890-abcd-1234567890ab"",""amount"":42.50,""currency"":""EUR"",""transactionRefId"":""b2c3d4e5-f678-90ab-cdef-1234567890ab"",""status"":""RECEIVED"",""note"":""new note2"",""createdAt"":""2025-08-01T10:15:30+03:00"",""updatedAt"":""2025-08-08T16:20:00+03:00""}"

// check method -> searchPayments
curl -G "http://localhost:8080/api/payments/search" ^
--data-urlencode "paymentStatus=DECLINED" ^
--data-urlencode "currency=USD" ^
--data-urlencode "minAmount=10.00" ^
--data-urlencode "createdAtAfter=2025-01-01T00:00:00Z" ^
--data-urlencode "page=0" ^
--data-urlencode "size=10" ^
--data-urlencode "sort=createdAt,desc"

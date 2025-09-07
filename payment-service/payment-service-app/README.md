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

//  check method -> create -> validation Guid
curl -v -X POST "http://localhost:8080/api/payments" ^
-H "Content-Type: application/json" ^
-d "{""guid"":""a1b2c3d4-e5f6-7890-abcd-1254567890ab"",""inquiryRefId"":""a1b2c3d4-e5f6-7990-abcd-1234567890ab"",""amount"":122.50,""currency"":""USD"",""transactionRefId"":""b2c3d1e5-f678-90ab-cdef-1234567890ab"",""status"":""DECLINED"",""note"":""new note"",""createdAt"":""2025-08-01T10:15:30+03:00"",""updatedAt"":""2025-08-08T16:20:00+03:00""}"

//  check method -> update
curl -v -X PUT "http://localhost:8080/api/payments/ac328a1a-1e60-4dd3-bee5-ed573d74c841" ^
-H "Content-Type: application/json" ^
-d "{""inquiryRefId"":""a1b2c3d4-e5f6-7890-abcd-1234567890ab"",""amount"":42.50,""currency"":""EUR"",""transactionRefId"":""b2c3d4e5-f678-90ab-cdef-1234567890ab"",""status"":""RECEIVED"",""note"":""new note2"",""createdAt"":""2025-08-01T10:15:30+03:00"",""updatedAt"":""2025-08-08T16:20:00+03:00""}"

// check method -> searchPayments
curl -G "http://localhost:8080/api/payments/search" ^
--data-urlencode "paymentStatus=DECLINED" ^
--data-urlencode "currency=EUR" ^
--data-urlencode "minAmount=10.00" ^
--data-urlencode "createdAtAfter=2025-01-01T00:00:00Z" ^
--data-urlencode "page=0" ^
--data-urlencode "size=10" ^
--data-urlencode "sort=createdAt,desc"


// bash
//получение JWT-токена

//для user_admin
curl -X POST "http://localhost:8085/realms/iprody-lms/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=basic_client" \
-d "client_secret=myclient-secret" \
-d "username=user_admin" \
-d "password=myPassword"

// для user_reader
curl -X POST "http://localhost:8085/realms/iprody-lms/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=basic_client" \
-d "client_secret=myclient-secret" \
-d "username=user_reader" \
-d "password=readerPassword"

ACCESS_TOKEN="generated_token_from_above_command"

// --------- for admin and reader ---------

// Get all payments
curl -v -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/api/payments/all"

// Get payment by id 
curl -v -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/api/payments/d552c963-4008-4eaa-bac2-9c7c4dd43f73"

// search payments
curl -v -G -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/api/payments/search" \
--data-urlencode "paymentStatus=DECLINED" \
--data-urlencode "currency=EUR" \
--data-urlencode "minAmount=10.00" \
--data-urlencode "createdAtAfter=2025-01-01T00:00:00Z" \
--data-urlencode "page=0" \
--data-urlencode "size=10" \
--data-urlencode "sort=createdAt,desc"

// --------- only for admin ---------- 

// patch update 
curl -v -X PATCH "http://localhost:8080/api/payments/d552c963-4008-4eaa-bac2-9c7c4dd43f73/note" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{"note":"new noteTest"}'

// update
curl -v -X PUT "http://localhost:8080/api/payments/d552c963-4008-4eaa-bac2-9c7c4dd43f73" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{
   "inquiryRefId":"a1b2c3d4-e5f6-7890-abcd-1234567890ab",
   "amount":42.50,
   "currency":"EUR",
   "transactionRefId":"b2c3d4e5-f678-90ab-cdef-1234567890ab",
   "status":"RECEIVED",
   "note":"new note2",
   "createdAt":"2025-08-01T10:15:30+03:00",
   "updatedAt":"2025-08-08T16:20:00+03:00"
}'

// delete
curl -v -X DELETE "http://localhost:8080/api/payments/123e4567-e89b-12d3-a456-426614174100" \
-H "Authorization: Bearer $ACCESS_TOKEN"

// create
curl -v -X POST "http://localhost:8080/api/payments" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{
   "inquiryRefId":"a1b2c3d4-e5f6-7990-abcd-1234567890ab",
   "amount":122.50,
   "currency":"USD",
   "transactionRefId":"b2c3d1e5-f678-90ab-cdef-1234567890ab",
   "status":"DECLINED",
   "note":"new note",
   "createdAt":"2025-08-01T10:15:30+03:00",
   "updatedAt":"2025-08-08T16:20:00+03:00"
}'


// ---------- actuator (health,info,metrics,env,loggers)----------

// Запрос данных о текущем состоянии
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/actuator/health"

// Список метрик
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/actuator/metrics"

// Значение каждой метрики из этого списка
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/actuator/metrics/<metric name>"

// Запросить информацию об HTTP запросах к приложению
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/actuator/metrics/http.server.requests"

//-----------------------kafka-------------
// в cmd
// docker exec -it kafka /opt/bitnami/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic demo

curl -v -X POST "http://localhost:8080/api/payments" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-d '{
"amount":122.50,
"currency":"USD",
"status":"DECLINED"
}'
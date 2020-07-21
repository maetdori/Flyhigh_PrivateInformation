JSON Request/Response Body Example
==================================

#### getList (POST /private/getList)

###### Request Body
<pre><code>{
  "username" : "LeeHaeIn"
}</code></pre>
*  username: OmniDoc Demo App 사용자 이름

###### Response Body
<pre><code>{
  "count": 2,
  "account":[
  {
    "subject": "LeeHaeIn",
    "validity":{
      "notAfter": "Thu Jun 02 09:00:00 KST 2022",
      "notBefore": "Wed May 31 09:00:00 KST 2017"
    }
  },  
  {
    "subject": "KimHaeIn",
    "validity":{
      "notAfter": "Thu Jun 02 09:00:00 KST 2022",
      "notBefore": "Wed May 31 09:00:00 KST 2017"
    }
  }
  ]
}</code></pre>


#### getInfo (POST /private/getInfo)

###### Request Body
<pre><code>{
  "pubKey" : "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh31XfhKQQM5fzC6M9qWqxqy0PZFWs8WUQpFg62errZxUcvclsaRtlTYCrvT+/HJ2jkMyqJTU+XxthyGKZJlC56n/BR6aDZ+yewVBRJfrumuUbkVyciS2QYg4cSx3CemYMXpxPKLCO+mR5KC+KHeEo1VOt1FowzxAnBvDU6ogxkF9hHR8erZlX1pKXzXnF2rYXENzhq2lU0tPC/PIsp8OcI8RuKeUu00e/cM5JvViA+4lj2GB64S1vgnnT3Y5pW68aXIqyl1CN5rM6GsE0TicSOeKT1EPCosnSDfbG+yQlolSmHZEKvLboz7SEl9lkAhoNfe0KfKlQBWKeByPGKJpkQIDAQAB",
  "cKey" : "PogxnZGcllEFo2yJOfOHiBtmZ8B6UfwDgY6k2bcvqr9GBY04ncY4z5ofALKXqegiigsXZhDkKfu8FoUfBc+HezoHeFaGEG3aXzaHsdQOaLFusvQGYw3W7YtOfMBhm4hpuwhviywYw0vY5Gkss+hQ32Vh2UODNDW+raU9arzTd5UOupI+JCIDwBQJvUq1Zn4rseI8UDRiOjTiNOq8hXBc0raAH+l90B4G1TweZ2hmancQcCqMHnVX7HSZmx3/IMi7EjdoXSYRhez1c3fzw6xIjVhJPPrXOni+z7lzr1ZrgIhKfdJbHQPJqn7dBdJSNUg2MlicRn9nlU9wRPhP9DfBZA==",
  "username" : "LeeHaeIn",
  "subject" : "KimHaeIn",
  "validity" : {
    "notAfter": "Thu Jun 02 09:00:00 KST 2022",
    "notBefore": "Wed May 31 09:00:00 KST 2017"
  }
}</code></pre>

###### Response Body
<pre><code>{
  "count": 2,
  "sKey": "gsLBVfAAu8TmSMcHYgZ1lzhOzyuhp4q7ABvskrPKvv0RAIcDr346PC/05jOUnyxQSVg9vPD5TxrFVpI2hlBLxf+ykH7ZmR2fFn8DEIipk5uaOA6Wxw09aHZrh4Kw6jf5TVsxkZUcd8q46QXfOWEyPJvrpqy8PIHxJFfamrtB7ZwRhogA5YcjG4328CkkAI8orD1I/XNXLztKfdVJN8tD6S8tG2FTKOD/KTKcHO3sraMzqM7Q3u1ve9+lfK/IYJ39EGEg6HFS2u1X7BB8u+k/Y3qHQNZoRnTUUoNJ0vVFrvtNdUV4ZtJzWvP5YlLnNMiUAwoCinAR/Ckei0jaG77zuA==",
  "encryptedElements":[
  {
    "name": "cert_pw"
  },
  {
    "name": "certification"
  },
  {
    "name": "account/site"
  },
  {
    "name": "account/id"
  },
  {
    "name": "account/pw"
  }
  ],
  "cert_pw": "s110tW3UZKuyP6epkrOexA==",
  "account":[
  {
    "site": "AAWMdh/P4qznENE5xPS7hg==",
    "pw": "WYXE8uxnASKkItYCVlZx9g==",
    "id": "Y7ez/65dq9LsGXFVNsQPKw=="
  },
  {
    "site": "D34DlJWAMB7AHWuRIrgBKA==",
    "pw": "WYXE8uxnASKkItYCVlZx9g==",
    "id": "Y7ez/65dq9LsGXFVNsQPKw=="
  }
  ],
  "certification": "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"
}</code></pre>

#### register (POST /private/register)

###### Request Body
<pre><code>{
  "pubKey" : "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh31XfhKQQM5fzC6M9qWqxqy0PZFWs8WUQpFg62errZxUcvclsaRtlTYCrvT+/HJ2jkMyqJTU+XxthyGKZJlC56n/BR6aDZ+yewVBRJfrumuUbkVyciS2QYg4cSx3CemYMXpxPKLCO+mR5KC+KHeEo1VOt1FowzxAnBvDU6ogxkF9hHR8erZlX1pKXzXnF2rYXENzhq2lU0tPC/PIsp8OcI8RuKeUu00e/cM5JvViA+4lj2GB64S1vgnnT3Y5pW68aXIqyl1CN5rM6GsE0TicSOeKT1EPCosnSDfbG+yQlolSmHZEKvLboz7SEl9lkAhoNfe0KfKlQBWKeByPGKJpkQIDAQAB",
  "cKey" : "PogxnZGcllEFo2yJOfOHiBtmZ8B6UfwDgY6k2bcvqr9GBY04ncY4z5ofALKXqegiigsXZhDkKfu8FoUfBc+HezoHeFaGEG3aXzaHsdQOaLFusvQGYw3W7YtOfMBhm4hpuwhviywYw0vY5Gkss+hQ32Vh2UODNDW+raU9arzTd5UOupI+JCIDwBQJvUq1Zn4rseI8UDRiOjTiNOq8hXBc0raAH+l90B4G1TweZ2hmancQcCqMHnVX7HSZmx3/IMi7EjdoXSYRhez1c3fzw6xIjVhJPPrXOni+z7lzr1ZrgIhKfdJbHQPJqn7dBdJSNUg2MlicRn9nlU9wRPhP9DfBZA==",
  "subject": "ChoiHaeIn",
  "cert_pw" : "pw123",
  "certification" : {
    "der" : "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",
    "key" : "key file"
  },
  "count" : 1,
  "account": [
    {
      "site" : "kb.com",
      "id" : "id_1",
      "pw" : "pw_1"
    }
  ]
}</pre></code>

###### Response Body
<pre><code>{
  "subject": "ChoiHaeIn",
  "count": 1,
  "validity":{
    "notAfter": "Thu Jun 02 09:00:00 KST 2022",
    "notBefore": "Wed May 31 09:00:00 KST 2017"
  },
  "registerDate": "2020-07-21"
}</pre></code>

#### modify (POST /private/modify)
수정중
###### Request Body
###### Response Body

#### delete (POST /private/delete)

###### Request Body
<pre><code>{
  "subject": "JeongHaeIn"
}</code></pre>

###### Response Body
<pre><code>{
  "subject": "JeongHaeIn"
}</code></pre>
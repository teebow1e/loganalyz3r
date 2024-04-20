# Parsing ModSecurity Log (`error.log`)
```
[Thu Apr 18 15:49:53.491056 2024] [:error] [pid 13086:tid 126645742585408] [client 127.0.0.1:40408] [client 127.0.0.1] ModSecurity: Warning. detected XSS using libinjection. [file "/usr/share/modsecurity-crs/rules/REQUEST-941-APPLICATION-ATTACK-XSS.conf"] [line "100"] [id "941100"] [msg "XSS Attack Detected via libinjection"] [data "Matched Data: XSS data found within ARGS:username: <script>alert(1);</script>"] [severity "CRITICAL"] [ver "OWASP_CRS/4.2.0-dev"] [tag "application-multi"] [tag "language-multi"] [tag "platform-multi"] [tag "attack-xss"] [tag "xss-perf-disable"] [tag "paranoia-level/1"] [tag "OWASP_CRS"] [tag "capec/1000/152/242"] [hostname "localhost"] [uri "/"] [unique_id "ZiDesaSiJ6EFvQS_LQjULAAAAEc"]
```

## Tham khảo
[https://www.claranet.com/us/blog/2020-10-30-continuous-security-monitoring-using-modsecurity-elk](https://www.claranet.com/us/blog/2020-10-30-continuous-security-monitoring-using-modsecurity-elk)

## Các thông tin nên lấy ra
- Timestamp: `[Thu Apr 18 15:49:53.491056 2024]`
- ipAddr - ip của attacker: `[client 127.0.0.1:40408]`
- phương thức tấn công: `[file "/usr/share/modsecurity-crs/rules/REQUEST-941-APPLICATION-ATTACK-XSS.conf"] -> REQUEST-941-APPLICATION-ATTACK-XSS`
- attackMsg: `[msg "XSS Attack Detected via libinjection"]`
- attackData - payload: `[data "Matched Data: XSS data found within ARGS:username: <script>alert(1);</script>"]`
- severity - độ nghiêm trọng: `[severity "CRITICAL"]`
- path: `[uri "/"]`
- unique_id (chưa rõ mục đích): `[unique_id "ZiDesaSiJ6EFvQS_LQjULAAAAEc"]`

## Regex để lấy các thông tin trên
> Các phương thức sử dụng `split` cần check nghĩ hiệu năng
- Timestamp: Split bằng dấu cách, lấy thằng index 0, sau đó crop đi cặp `[]`
- ipAddr: Split bằng dấu cách, lấy thằng index 3, sau đó crop đi cặp `[]`, dùng regex tiếp để lấy IP + PORT
- attackType: `\[file ".+\/(.*?).conf"\] -> [A-Z][^.]+`
- attackMsg: `\[msg "(.*?)"\]`
- attackData: `\[data "(.*?)"\]`
- severity: `\[severity "(.*?)"\]`
- uri: `\[uri "(.*?)"\]`
- uid: `\[unique_id "(.*?)"\]`
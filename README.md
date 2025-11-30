# RSA Encryption/Decryption Library

Thư viện RSA với các tính năng bảo mật nâng cao (OAEP padding) và tối ưu hóa hiệu suất (CRT decryption).

---

## 📋 Mục Lục

1. [Giới thiệu Interface RSACipher](#-giới-thiệu-interface-rsacipher)
2. [Cấu trúc dự án](#-cấu-trúc-dự-án)
3. [Hướng dẫn chạy](#-hướng-dẫn-chạy)
4. [Cách chương trình hoạt động](#-cách-chương-trình-hoạt-động)
5. [Ví dụ code](#-ví-dụ-code)

---

## 🎯 Giới thiệu Interface RSACipher

### Tại sao cần Interface?

`RSACipher` là interface định nghĩa **contract** cho các phương thức mã hóa/giải mã RSA. Interface giúp:

✅ **Rõ ràng**: Developer dễ dàng biết các phương thức có sẵn  
✅ **Type Safety**: Compiler kiểm tra implementation tại compile-time  
✅ **Autocomplete**: IDE gợi ý phương thức chính xác  
✅ **Mở rộng**: Dễ tạo implementation mới (ví dụ: RSAUtilsOptimized)  
✅ **Documentation**: Interface tự documenting với JavaDoc

### Các phương thức trong Interface

| Phương thức              | Mô tả                              | Khuyến nghị                   |
| ------------------------ | ---------------------------------- | ----------------------------- |
| `encrypt()`              | Mã hóa RSA cơ bản: c = m^e mod n   | ⚠️ Chỉ dùng cho demo          |
| `decrypt()`              | Giải mã RSA cơ bản: m = c^d mod n  | ⚠️ Chỉ dùng cho demo          |
| `encryptOAEP()`          | Mã hóa với OAEP padding (bảo mật)  | ✅ Khuyến nghị cho production |
| `decryptOAEP()`          | Giải mã với OAEP padding           | ✅ Khuyến nghị cho production |
| `decryptCRT()`           | Giải mã nhanh với CRT (~4x faster) | ⚡ Tối ưu hiệu suất           |
| `decryptOAEP_CRT()`      | Giải mã nhanh + bảo mật            | ⭐ **RECOMMENDED**            |
| `encrypt(String/byte[])` | Mã hóa String hoặc byte[]          | ✅ Tiện lợi                   |
| `decryptToString()`      | Giải mã ra String                  | ✅ Tiện lợi                   |
| `decryptToBytes()`       | Giải mã ra byte[]                  | ✅ Tiện lợi                   |

---

## 📁 Cấu trúc dự án

### Core Files (Phần chính)

| File                      | Vai trò                 | Mô tả                                      |
| ------------------------- | ----------------------- | ------------------------------------------ |
| **RSACipher.java**        | 🎯 **Interface chính**  | Định nghĩa contract cho mã hóa/giải mã RSA |
| **RSAUtils.java**         | 🔐 **Implementation**   | Triển khai RSACipher với OAEP & CRT        |
| **KeyPair.java**          | 🔑 **Key Generation**   | Tạo cặp khóa RSA (random/strong)           |
| **Utils.java**            | 🛠️ **Helper Utilities** | Modular exponentiation, GCD, XOR, etc.     |
| **PrimeGenerator.java**   | 🎲 **Prime Generator**  | Tạo số nguyên tố lớn cho RSA               |
| **RSAPrimeVerifier.java** | ✅ **Prime Verifier**   | Kiểm tra tính hợp lệ của p, q cho RSA      |

### Demo & Test Files

| File                      | Vai trò                 | Mô tả                                |
| ------------------------- | ----------------------- | ------------------------------------ |
| **Main.java**             | 🚀 **Demo cơ bản**      | Demo encrypt/decrypt với OAEP + CRT  |
| **ImprovementsDemo.java** | 📊 **Demo nâng cao**    | Benchmark OAEP, CRT, Strong KeyPair  |
| **StringByteDemo.java**   | 🔤 **Demo String/Byte** | Demo mã hóa/giải mã String và byte[] |
| **Test.java**             | 🧪 **Testing**          | Unit tests cho các components        |

### Scripts & Documentation

| File                    | Vai trò         | Mô tả                         |
| ----------------------- | --------------- | ----------------------------- |
| **run.sh**              | ▶️ **Run Main** | Script chạy chương trình Main |
| **run_improvements.sh** | ▶️ **Run Demo** | Script chạy ImprovementsDemo  |
| **run_string_byte.sh**  | ▶️ **Run Demo** | Script chạy StringByteDemo    |

---

## 🚀 Hướng dẫn chạy

### 1. Di chuyển vào thư mục mã nguồn

```bash
cd srcs
```

### 2. Compile tất cả files

```bash
javac *.java -d bin
```

### 3. Chạy chương trình chính (Main)

```bash
./run.sh
```

**Chương trình sẽ yêu cầu nhập:**

- Modulus bit-length (ví dụ: 512, 1024)
- Message để mã hóa (số nguyên dương)

### 4. Chạy Improvements Demo

```bash
./run_improvements.sh
```

**Demo sẽ showcase:**

- [1] Strong Key Generation performance
- [2] OAEP Encryption randomness test
- [3] CRT Decryption speed comparison

### 5. Chạy String/Byte Demo

```bash
./run_string_byte.sh
```

**Demo sẽ showcase:**

- [1] String Encryption/Decryption
- [2] Byte[] Encryption/Decryption

---

## 💻 Cách chương trình hoạt động

### Bước 1: Tạo RSAUtils instance

```java
// RSAUtils implement RSACipher interface
RSAUtils rsaUtils = new RSAUtils();
```

### Bước 2: Tạo Key Pair

```java
// Tạo strong key pair (khuyến nghị cho production)
KeyPair keyPair = KeyPair.generateStrongKeyPair(1024);

// Hoặc tạo random key pair (nhanh hơn, cho testing)
// KeyPair keyPair = KeyPair.generateRandomKeyPair(1024);
```

### Bước 3: Mã hóa

```java
BigInteger message = new BigInteger("12345");

// Mã hóa với OAEP padding (bảo mật)
BigInteger cipher = rsaUtils.encryptOAEP(
    message,
    keyPair.getEncryptKey(),
    keyPair.getModulus()
);
```

### Bước 4: Giải mã

```java
// Giải mã với OAEP + CRT (nhanh và bảo mật - RECOMMENDED)
BigInteger decrypted = rsaUtils.decryptOAEP_CRT(cipher, keyPair);

// Hoặc giải mã chỉ với OAEP (không dùng CRT)
// BigInteger decrypted = rsaUtils.decryptOAEP(cipher, keyPair.getDecryptKey(), keyPair.getModulus());
```

### Bước 5: Mã hóa/Giải mã String & Byte[] (Mới)

```java
// Mã hóa String
String text = "Hello RSA!";
BigInteger cipherText = rsaUtils.encryptOAEP(text, keyPair.getEncryptKey(), keyPair.getModulus());

// Giải mã ra String
String decryptedText = rsaUtils.decryptOAEP_CRTToString(cipherText, keyPair);
System.out.println(decryptedText); // "Hello RSA!"

// Mã hóa byte[]
byte[] data = {1, 2, 3};
BigInteger cipherBytes = rsaUtils.encryptOAEP(data, keyPair.getEncryptKey(), keyPair.getModulus());

// Giải mã ra byte[]
byte[] decryptedBytes = rsaUtils.decryptOAEP_CRTToBytes(cipherBytes, keyPair);
```

---

## 📝 Ví dụ code

### Ví dụ 1: Encrypt & Decrypt cơ bản với OAEP

```java
import java.math.BigInteger;

public class BasicExample {
    public static void main(String[] args) {
        // 1. Tạo instance
        RSAUtils rsaUtils = new RSAUtils();

        // 2. Tạo key pair
        KeyPair keyPair = KeyPair.generateStrongKeyPair(1024);

        // 3. Message
        BigInteger message = new BigInteger("999999");

        // 4. Encrypt với OAEP
        BigInteger cipher = rsaUtils.encryptOAEP(
            message,
            keyPair.getEncryptKey(),
            keyPair.getModulus()
        );

        // 5. Decrypt với OAEP + CRT
        BigInteger decrypted = rsaUtils.decryptOAEP_CRT(cipher, keyPair);

        // 6. Verify
        System.out.println("Original:  " + message);
        System.out.println("Decrypted: " + decrypted);
        System.out.println("Match: " + message.equals(decrypted));
    }
}
```

### Ví dụ 2: So sánh hiệu suất CRT vs Standard

```java
import java.math.BigInteger;

public class PerformanceExample {
    public static void main(String[] args) {
        RSAUtils rsaUtils = new RSAUtils();
        KeyPair keyPair = KeyPair.generateStrongKeyPair(2048);

        BigInteger message = new BigInteger("12345");
        BigInteger cipher = rsaUtils.encryptOAEP(
            message,
            keyPair.getEncryptKey(),
            keyPair.getModulus()
        );

        int iterations = 100;

        // Standard decryption
        long startStd = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            rsaUtils.decryptOAEP(cipher, keyPair.getDecryptKey(), keyPair.getModulus());
        }
        long timeStd = System.currentTimeMillis() - startStd;

        // CRT decryption
        long startCRT = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            rsaUtils.decryptOAEP_CRT(cipher, keyPair);
        }
        long timeCRT = System.currentTimeMillis() - startCRT;

        System.out.println("Standard: " + timeStd + "ms");
        System.out.println("CRT:      " + timeCRT + "ms");
        System.out.printf("Speedup: %.2fx faster\n", (double) timeStd / timeCRT);
    }
}
```

---

## 🔒 Best Practices

### ✅ Khuyến nghị

1. Sử dụng `generateStrongKeyPair()` cho production
2. Sử dụng `encryptOAEP()` thay vì `encrypt()` cơ bản
3. Sử dụng `decryptOAEP_CRT()` để có cả tốc độ và bảo mật
4. Dùng modulus ≥ 2048 bits cho bảo mật tốt

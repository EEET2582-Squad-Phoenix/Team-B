# HOW TO RUN
```bash
Important: If all steps are done correctly but BE still does not run, run ./mvnw clean install
step 1: run ./mvnw install
step 2: run ./mvnw spring-boot:run -pl application
```

**Testing accounts**
```bash
ADMIN
    "email": "admin@gmail.com"
    "password": "admin-password123"

CHARITY
    Vietnam - Indiviual                           Germany - Company
    "email": "HopeFoundation@gmail.com"           "email": "BooksForAll@gmail.com"
    "password": "Hope1234!"                       "password": "Books4U!23"
    
    USA - Individual                              Ukraine - Non-profit
    "email": "GreenEarthCharity@gmail.com"        "email": "AnimalCareOrg@gmail.com"
    "password": "Earth@2025"                      "password": "Animals2023@"

    South Africa - Company                        Israel - Non-profit
    "email": "FeedTheWorld@gmail.com"             "email": "SaveOurOceans@gmail.com"
    "password": "Feeding#123"                     "password": "OceanLove!456"

DONOR
    Vietnamese                                German
    "email": "vietnam.donor1@gmail.com",      "email": "germany.donor1@gmail.com",
    "password": "VND@12345",                  "password": "GermanyD1@2024",
    
    "email": "vietnam.donor2@gmail.com",      "email": "germany.donor2@gmail.com",
    "password": "VietDonor2@",                "password": "DonorDE2#",

    "email": "vietnam.donor3@gmail.com",      "email": "germany.donor3@gmail.com",
    "password": "TuanPham@2024",              "password": "DEDonor@321",

    "email": "vietnam.donor4@gmail.com",      "email": "germany.donor4@gmail.com",
    "password": "DonorVN$5678",               "password": "DonorGER4@",

    "email": "vietnam.donor5@gmail.com",      "email": "germany.donor5@gmail.com",
    "password": "VNHero@567",                 "password": "GermDonor5!",

    "email": "john.doe.donor@gmail.com"
    "password": "JohnDoe123!"
```

**Docker commands**
```bash
Important: redirect to Team-B\Redis-docker
docker compose up
docker compose down
```

# Documents

## Diagram
- [ER Model](https://drive.google.com/file/d/1tArlar1WjgZ1oUrVpfaJFV0U0w96rRU0/view?usp=sharing)
- [C4 Model Component Diagram](https://online.visual-paradigm.com/share.jsp?id=333730313436302d31)

## Report
- [Milestone 1 report](https://docs.google.com/document/d/1HjZYw-9ZXzuMYkLFFy9G0__hgKjT8WVQgwS1lFFQ79c/edit?usp=sharing)
- [Project Charter](https://rmiteduau-my.sharepoint.com/:w:/r/personal/s3907397_rmit_edu_vn/Documents/Project%20Charter%20-%20Team%20B%20-%20Squad%20Phoenix.docx?d=w6de95e58fc9540169d3ee57f6c834b67&csf=1&web=1&e=8yQRdi)
- [Contribution Declaration](#)

## Figma
- [Figma design](https://www.figma.com/design/rwnx3u5SUsMKPeCiNw8CUI/EEET2582---Team-B?node-id=0-1&t=uUpvmsFMPzEMcIhm-1)


## Backend
Spring Boot 3.4.0
Java 21

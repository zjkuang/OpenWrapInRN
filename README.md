# OpenWrapInRN

## Setup

(1) Clone the repository to your local machine
```
git clone https://github.com/zjkuang/OpenWrapInRN.git
```

(2) Change your working directory to the cloned repository
```
cd OpenWrapInRN
```

(3) Create `android/local.properties` file  
Create `android/local.properties` and follow [this](https://stackoverflow.com/a/54234228/7455975) to indicate the location of the Android Studio SDK installation (which may be different on different machines).

(4) Create .secret file  
```
{
  "com.google.android.gms.ads.APPLICATION_ID": "<your-google-gms-app-id>",
  "PubMatic_PublisherID": "<your-publisher-id>",
  "PubMatic_ProfileID": <your-profile-id>,
  "HomeAnchorPortrait": {
    "SlotUUId": "<slot-uuid-for-home-anchor-ad>",
    "NetworkId": <network-id>,
    "NetworkKey": "<network-key-for-home-anchor-ad>",
    "UnitId": "<unit-id-for-home-anchor-ad"
  },
  "ListInside": {
    "SlotUUId": "<slot-uuid-for-list-inside2-ad>",
    "NetworkId": <network-id>,
    "NetworkKey": "<network-key-for-list-inside2-ad>",
    "UnitId": "<unit-id-for-list-inside2-ad>"
  },
  "ListBottom": {
    "SlotUUId": "<slot-uuid-for-list-bottom-ad>",
    "NetworkId": <network-id>,
    "NetworkKey": "<network-key-for-list-bottom-ad>",
    "UnitId": "<unit-id-for-list-bottom-ad>"
  }
}
```

(5) Install the packages  
(in OpenWrapInRN root directory)
```
yarn install
```

(6) Run the app  
(in OpenWrapInRN root directory)
```
yarn android
```
(iOS part is not done yet.)

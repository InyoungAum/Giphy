# Giphy

- Giphy앱의 모작입니다.

## Description

- Main(Search, Favorites), Detail의  화면으로 구성되어 있습니다.

### Search
 - 검색어를 통해 Giphy의 이미지를 검색합니다.
 - 최초 15개씩 로드합니다.
 - 하단으로 스크롤을 하여 다음 페이지의 이미지를 로드합니다.
 - 이미지를 클릭하면 Detail View로 이동합니다.

### Detail
  - Search와 Favorites에서 각 이미지를 클릭하면 이동합니다.
  - 우측 상단의 버튼으로 이미지를 보관하거나 보관함에서 삭제합니다.

### Favorites
  - Detail에서 '좋아요'한 이미지들의 목록입니다.
  - ViewModel과 LiveData를 통해 Realm의 결과가 바뀔 때 자동으로 이미지 목록을 업데이트 합니다.

## Library
  - Glide : GIF Image 로드
  - Retrofit, OKHTTP3 : Giphy API Call
  - Realm : '좋아요'한 이미지 저장

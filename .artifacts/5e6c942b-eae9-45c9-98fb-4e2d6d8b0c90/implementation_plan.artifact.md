# Plantopedia Crop Selection Feature Implementation Plan

Implement crop selection in Plantopedia allowing users to select multiple crops they grow (Tomato, Rice, Wheat, Cotton, Sugarcane, Potato, Apple, Corn) with selectable cards, persistent storage associated with the user account, and a single interactive **"My Crops"** / **"Select Your Crops"** section on the Home screen that serves for both viewing and editing.

## Proposed Changes

### Storage & Data (`UserManager.kt`)

#### [MODIFY] [UserManager.kt](file:///C:/Users/bhavi/AndroidStudioProjects/PLANTOPEDIA/app/src/main/java/com/example/plantopedia/UserManager.kt)
- Add `getSelectedCrops(context: Context): Set<String>` and `saveSelectedCrops(context: Context, crops: Set<String>)` associated with the active user/guest profile in SharedPreferences.

---

### String Resources (`strings.xml`, `values-hi/strings.xml`, `values-mr/strings.xml`)

#### [MODIFY] [strings.xml](file:///C:/Users/bhavi/AndroidStudioProjects/PLANTOPEDIA/app/src/main/res/values/strings.xml)
#### [MODIFY] [strings.xml](file:///C:/Users/bhavi/AndroidStudioProjects/PLANTOPEDIA/app/src/main/res/values-hi/strings.xml)
#### [MODIFY] [strings.xml](file:///C:/Users/bhavi/AndroidStudioProjects/PLANTOPEDIA/app/src/main/res/values-mr/strings.xml)
- Add localized strings for "My Crops", "Select Your Crops", crop names (Tomato, Rice, Wheat, Cotton, Sugarcane, Potato, Apple, Corn), and "Save Crops".

---

### Home Screen & Crop Selection Dialog (`HomeScreen.kt`)

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/bhavi/AndroidStudioProjects/PLANTOPEDIA/app/src/main/java/com/example/plantopedia/HomeScreen.kt)
- Add available crops model and localization helper.
- Add a single **"My Crops"** section on the Home screen:
  - If no crops are selected, display **"Select Your Crops"** with a `+` icon, making the card clickable to open the crop selection dialog.
  - If crops are selected, display the user's selected crops as clean cards/chips with emojis and names, along with an edit/`+` icon. Clicking anywhere on the section opens the crop selection dialog to add or remove crops.
- Create a Crop Selection Dialog matching the Plantasia theme (`#FAF8F2` warm white cards, `#E9E2D5` warm beige background, `#23443D` forest green) with checkboxes and a "Save Crops" button.

---

## Verification Plan

### Automated Tests
- Run `gradle_build("app:assembleDebug")` to verify compilation.

### Manual Verification
- Verify crop selection dialog opens from Home screen, saves selections, persists across app restarts, supports English, Hindi, and Marathi, and matches the Plantasia theme.

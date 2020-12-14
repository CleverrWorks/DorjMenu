# Dorj Menu Library

![GitHub release (latest by date)](https://img.shields.io/github/v/release/CleverrWorks/dorjmenu)
![GitHub](https://img.shields.io/github/license/CleverrWorks/dorjmenu?style=flat)
![GitHub last commit](https://img.shields.io/github/last-commit/CleverrWorks/Dorjmenu)

A Drawer library that supports List and Page style menu Items, RTL Layout, Dark Mode, Full customizability and Ease of Use.  

Sample App:  
[![DorMenu Sample App](https://imgur.com/czX9JUY.png)](http://www.youtube.com/watch?v=vfPkVPk7MCc)

All Implementation and example is demonstrated in [sample app](app/src/main/java/mohammed/taosif7/dorjmenu_sample/MainActivity.java)
  
- [Dorj Menu Library](#dorj-menu-library)
  - [Installation](#installation)
  - [Adding DorjMenu to activity](#adding-dorjmenu-to-activity)
  - [Controlling DorjMenu](#controlling-dorjmenu)
  - [Setting up Menu Items](#setting-up-menu-items)
  - [Handling Menu Item clicks](#handling-menu-item-clicks)
  - [Customising The Header](#customising-the-header)
  - [Persistent Button](#persistent-button)
  - [CTA Button](#cta-button)
  - [RTL Layout](#rtl-layout)
  - [Other Customisations](#other-customisations)
  - [License & Copyright](#license--copyright)

<br>

---

<br>

## Installation

Add This in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Add the dependency to your app's build.gradle file

```java
dependencies {
  implementation 'com.github.cleverrworks:dorjmenu:1.0.0'
}
```


<br>
<br>

## Adding DorjMenu to activity

```java
// Create instance of DorjMenu
DorjMenu menu = new DorjMenu(
    this, /* Context */ 
    this /* DrawerCallbacks Listener interface*/
    );


...
// Every other customisation must be done before attaching to activity


// Attach the instance to activity
menu.attachToActivity(
     this, /* Activity */ 
     DorjMenu.direction.LEFT /* Direction to attach */
     );
```

<br>
<br>

## Controlling DorjMenu

To open menu programatically

```java
menu.openMenu();
```

To close menu programatically

```java
menu.closeMenu();
```

To toggle open/close menu programatically

```java
menu.toggleMenu();
```

<br>
<br>

## Setting up Menu Items

Menu Items can be set by calling `setItems(List<menuItem>)`

This function takes list of [`menuItem`](DorjMenu/src/main/java/mohammed/taosif7/dorjmenu/models/menuItem.java). menuItem is a node-like object that means it can have child and parent, thus you can construct a submenu.  

To add a child to a menuItem, call `addSubItem(menuItem)` or to add multiple children call `addSubItems(menuItem1, menuItem1, menuItem3, ...)`

```java
// Menu Item with drawable icon
MenuItem main1 = new MenuItem("Main Item 1", myDrawableIcon);
MenuItem main2 = new MenuItem("Main Item 2", myDrawableIcon);

// Menu Item with Drawable Resource ID
MenuItem sub1 = new MenuItem("Sub Item 1", R.drawable.my_icon, context);

// Adding sub items to sub1
sub1.addSubItems(
    new MenuItem("Sub sub item 1", R.drawable.my_ic, context),
    new MenuItem("Sub sub item 2", R.drawable.my_ic, context),
    new MenuItem("Sub sub item 3", R.drawable.my_ic, context),
    new MenuItem("Sub sub item 4", R.drawable.my_ic, context)
);

// Adding sub1 to main1
main1.addSubItem(sub1);

// Add main Items to a list
List<menuItem> menuItems = new ArrayList<>(){{
    add(main1);
    add(main2);
}}

// Call method to set the menu to drawer
dorjmenuInstance.setItems(menuItems);
```

This will construct the menu as:

> * Main Item 1
>   * Sub Item 1
>     * Sub sub item 1
>     * Sub sub item 2
>     * Sub sub item 3
>     * Sub sub item 4
> * Main Item 2

This way, you can construct menu upto any level, They'll be displayed as submenu or as Pages depending upon type that you set.   

To set the menu type, call
> menu.`setMenuType(MenuType)`;

<br>
<br>

## Handling Menu Item clicks

When you create instance of the drawer, its second argument is [`DrawerCallbacks`](DorjMenu/src/main/java/mohammed/taosif7/dorjmenu/helpers/DrawerCallbacks.java) which is an interface. So either you can implement that to your Activity class or create anonymous class and supply it as an argument. (See [Sample App](app/src/main/java/mohammed/taosif7/dorjmenu_sample/MainActivity.java))  

When you click an item in menu, there are two cases:

- If Menu is SubList type :
  - If Item has sub-item : `Menu Will Expand Sub menu`
  - If Item has no sub-item : `onDrawerMenuItemClick(menuItem)` *will be called*
- If Menu is Pages type :
  - If Item has sub-item : `Menu Will display page with sub-Items`
  - If Item has no sub-item : `onDrawerMenuItemClick(menuItem)` *will be called*
  
<br>
When menu item is clicked, below method is called in and It provides the clicked menuItem in the argument.

```java
boolean onDrawerMenuItemClick(menuItem clickedItem)
```

This method needs to return a boolean.  
If returned `true` The Item will be highlighted in the menu  
If returned `false` The Item will *not* be highlighted in the menu  

<br>

The highlighted menu item (by default) will have background colour of accent colour of menu. Menu Accent colour can be customised (see [Other Customisations](#other-customisations)). Explicity, you can set the item highlight colour anytime during runtime, by calling below method

```java
drawerMenu.setItemHighlightColor(int color) // Needs resolved colour int
```

Since this method can be called anytime during runtime, you can use it smartly to set different highlight colour for each item. (See [Sample App](app/src/main/java/mohammed/taosif7/dorjmenu_sample/MainActivity.java))

<br>
<br>

## Customising The Header

![Header content explaination](https://imgur.com/DMv5s20.png)

To set the profile picture, email & username:

```java
menu.setUserDetails(
    profile_pic, // Drawable for Profile pic image, can be null
    "Username", // Can be null
    "Email", // Can be null
    true, // Show placeholder if username/email is null and profile_pic is not provided
);
```

By Default, Header will have a background Image, to change it:

```java
menu.setHeaderBackground(Drawable);
```

The Header background has shade to it, if you want to remove it:

```java
menu.showHeaderShadow(boolean);
```

To set the Header Button:

```java
menu.setHeaderButton(Drawable icon, View.OnClickListener action);
```

<br>
<br>

## Persistent Button

![Persistent Button Location](https://imgur.com/5KN7bZc.png)

Persistent button is visually menuItem button shown at the bottom of drawer and is visible at all the time. to set a persistent button,

```java
menu.setPersistentButton(
    @Nullable Drawable icon, // Icon to set
    String label, // Label of button
    View.OnClickListener action // Action on click
);
```

<br>
<br>

## CTA Button

![CTA Button Location](https://imgur.com/1tYzbWB.png)

A Call To Action (CTA) Button is a button that attracts user to take an action. This button is shown above the persistent button (if visible) or at the bottom of the drawer.

```java
menu.setCTAButton(
    String label, // Label of button
    @Nullable Integer buttonColor, // int Colour of button background
    @Nullable View.OnClickListener action // Action on click
);
```

If Button colour is not provided, menu's Accent colour is used. Menu Accent colour can be customised (see [Other Customisations](#other-customisations))

<br>
<br>

## RTL Layout

Rigth to Left (RTL) layout can be enabled if the Menu Items are in RTL Languages like Arabic, Hebrew, Persian, etc. By default, the menu should automatically adapt to RTL if App's layout mode is RTL. But you can enforce RTL Layout of the menu by:

```java
// Provide true to enforce RTL, false to enforce LTR
menu.forceRTLLayout(Boolean enable);
```

<br>
<br>

## Other Customisations

To set the amount of screen that is covered by menu (value can be between 0.6 to 1.0 (percentage of screen))  

```java
menu.setMenuOpenFactor(double percent);
```  

To set the duration of menu open/close animation

```java
menu.setGlideDuration(long duration);
```

Menu has an accent colour which is applied to various parts by default such as CTA Button or Item highlight colour (more in future). To change the colour:

```java
menu.setMenuAccentColor(int resolved_color);
```

<br>
<br>

## License & Copyright

© Mohammed Ahmed & Taosif Jamal

Licensed under the [MIT LICENSE](LICENSE).

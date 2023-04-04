# Godot Android Window Settings Plugin
This is an Android plugin for Godot 3.2.2 or higher.
Tested with Godot 3.5.2, but should work fine with other versions including 4.0 if built properly.
Check releases for 'official' builds.

This plugin enables you to access a number of useful Android window properties and UI flags through gdscript.
* Set custom colors for navigation and status bars
* Toggle immersive mode (auto-hiding bars) during runtime
* Access notch/cutout data for more dynamic UI
* Address bars individually and at any time
* Compatible with 9 years of android versions, hassle free\*

## How to use
1. In your Godot project, install android build template. You can follow the [official documentation](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html) for more detail.
2. Go to Releases (on the right of the repo page) and download a build. You will need both the .gdap and .release.aar files!
3. On Godot platform choose: `Project -> Export -> Options`, and enable `Use Custom Build`, then `Plugins -> Godot Android Window Settings`
4. Set `Custom Build -> Min Sdk` to 21 or higher
5. Reference the `GodotAndroidWindowSettings` singleton from any script. *(The plugin does nothing unless told!)*


## Example in GDScript
```
# These functions need not be called constantly, but some things don't apply until after _ready().
# A more reasonable use case would be to change properties on a button press or scene transition.
func _process(_delta):
	if (Engine.has_singleton("GodotAndroidWindowSettings")):
		print("Setting initial window settings...")
		
		var ws = Engine.get_singleton("GodotAndroidWindowSettings")
		
		if ws.getApiLevel() >= 30:
			print("Insets are currently T%d, B%d, L%d, R%d" % \
			[ws.getInsetTop(),ws.getInsetBottom(),ws.getInsetLeft(),ws.getInsetRight()])
		else:
			print("No inset data! Running on Api Level: "+str(ws.get_api_level()))
		ws.setEnableCustomBarColors(true)
		ws.setStatusBarColor("#885522")
		ws.setNavigationBarColor("#225588")
		
	else:
		print("Window settings plugin not detected. Are you on android with correct build settings?")
```

## Technical Info
Many functions use the insetsController class introduced in Android R (api level 30), but contain fallbacks that work down to Android Lollipop (api level 21). The fallbacks should work fine, but have not been thoroughly tested. 

## Building from Source
1. Install and configure [Android Studio](https://developer.android.com/studio)
2. Clone this repository
3. Download the **AAR library (standard)** for your desired version from the [Godot download page](https://godotengine.org/download/) or the [previous version repository](https://downloads.tuxfamily.org/godotengine/)
4. Place the .aar file into the `lib` folder of the cloned repository, and rename it to `godot-lib.release.aar` exactly
5. Execute `./gradlew build`
The resulting file is `./window-plugin/build/outputs/aar/window-plugin-release.aar`. Copy it to `android/plugins` in your project. You will also need a .gdap file to tell Godot where to look. Use one from releases and set the binary accordingly.


# Full Documentation
**All setter functions have corresponding getter functions.**

For example, setTransparentBars takes a bool, and has a corresponding getTransparentBars function that returns a bool.

Note that all functions use camelCase, and not snake_case.

## Common Functions


#### setImmersive(bool)
*Switches between SYSTEM_UI_FLAG_IMMERSIVE and SYSTEM_UI_FLAG_VISIBLE*

If true, status and navigation bars will only be shown on swipe.

This behaves identically to "immersive mode" in Godot's android export settings. Make sure to *disable* the export setting if you want to use this, though. Call this function frequently, or else immersive mode my be disabled by the OS.

It uses different code for newer and older versions of Android, but should function identically.

#### setLightStatusBar(bool)
*Sets APPEARANCE_LIGHT_STATUS_BARS on or off.*

If true, status bar will have dark icons on a light background

If false, status bar will have light icons on a dark background



#### setLightNavigationBar(bool)
*Sets APPEARANCE_LIGHT_NAVIGATION_BARS on or off*

If true, navigation bar will have dark icons on a light background.
If false, navigation bar will have light icons on a dark background.
Use with 


#### setEnableCustomBarColors(bool)
*Sets the FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag*

This makes the window drag status and navigation bar background, allowing their color to be customized


#### setStatusBarColor(String)
Set the color of the status bar background to a hex code of your choice. ("#885522", for example)
Only works when custom bars are enabled!

#### setNavigationBarColor(String)
Set the color of the navigation bar background to a hex code of your choice. ("#225588", for example)
Only works when custom bars are enabled!

*To convert Godot Color to String:*
`'#'+color.to_html(false)`

## Advanced Functions
*These functions provide additional functionality, but might be hard to use or locked to newer android versions.*

#### setTransparentBars(bool) SET/GET
If true, sets status & navigation bar colors to transparent, and sets the FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag.
If false, reverts to previous settings.

Unfortunately, this doesn't really do anything, as Godot renders nothing below the bars.

#### setStatusBarVisible(bool) SET ONLY
Sets the visibility of the status bar. NO GETTER / REQUIRES API 30 (R)

#### setNavigationBarVisible(bool) SET ONLY
Sets the visibility of the naviagtion bar. NO GETTER / REQUIRES API 30 (R)

#### setCutoutVisible(bool) SET ONLY
Sets the visibility of display cutout(s), such as a black margin to account for a notch. NO GETTER / REQUIRES API 30 (R)

#### getInsetTop() -> float  GET ONLY
Gets the inset region on the top of the screen, after system bars and cutouts.  NO SETTER / REQUIRES API 23 (M)

#### getInsetBottom() -> float  GET ONLY
Gets the inset region on the bottom of the screen, after system bars and cutouts.  NO SETTER / REQUIRES API 23 (M)

#### getInsetLeft() -> float  GET ONLY
Gets the inset region on the left of the screen, after system bars and cutouts.  NO SETTER / REQUIRES API 23 (M)

#### getInsetRight() -> float  GET ONLY
Gets the inset region on the right of the screen, after system bars and cutouts.  NO SETTER / REQUIRES API 23 (M)

#### getApiLevel() -> int  GET ONLY
Gets your current Android api level. Useful for determining if certain features are accessible  NO SETTER
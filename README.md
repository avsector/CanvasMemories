## Canvas Memories

> "Sometimes you will never know the value of a moment until it becomes a memory" - Dr. Suess

Life is full of moments that we will always keep in our hearts. These memories give us joy when we remember them and they are our go tos in the time of sadness.
Now that you're here reading this, it wouldn't hurt to think of one of the memories that you hold dear.
Right about now, try to think of an extension to the class of your memories.

### So... what is the repo about?
"Canvas memories" is just a simple app to show some interactions with the canvas. Those are Add, Remove or Mutate simple shapes with the support of undoing.

### How it is structured

Completely written in Kotlin. The architecture is MVVM which is implemented using Android architecture components (ViewModel, LiveData, and LifeCycle).
Interactions are stored using a SparseArray to reduce the memory footprint and actions are kept safe in a Stack for easy retrieving. The app leverages the usage of Kotlin Collection APIs to achieve readability.
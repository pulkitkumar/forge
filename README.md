# forge

## An annotation processor to create factory for ViewModels in android.

Google announced the android architecture components in Google IO 2017. 
The ViewModels in the architecture components need a Factory. 

It is easy to use view model factory if you are using dagger 2.11 or above. 
You can use it like [this](https://github.com/tuann10/android-mvvm-architecture/blob/e4d8420728bee0ad11dfc77487c2bd0987a27884/app/src/main/java/com/tuann/mvvm/di/ViewModelFactory.kt).

But for codebases that do not use Dagger, or do use a lower version of dagger. There is a need to write a factory for each ViewModel. 

To solve that I have written this annotation processor. 

To avoid that we can just use the ```@Forge ``` annotation on the constructor of the ViewModel implementation class. The annotation would generate a factory class for the ViewModel with the constructor which has the annotation.

As can be seen in the example in the ```app``` module, the ```SomeViewModel``` class has the ```@Forge``` annotation on it's constructor. While building the project a class named ```SomeViewModel_Factory``` is created implementing the ```ViewModelProvider.Factory``` and creating the view model with all the needed params whenever needed.


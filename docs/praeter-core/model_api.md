# Model API
The Model API allows you to create and modify JSON block and item models.

The API provides wrappers around the raw JSON that allow you to change the JSON
via methods on the wrappers. It is therefore recommended to have some knowledge
about the JSON format used, since the Javadoc for this API does not thoroughly
describe concepts. Things like min and max values are not documented here. Refer
to the Minecraft Wiki, or similar, for an explanation of the JSON format.

The API allows you to auto generate models, for example to create animation
keyframes, to create variants of the same model, etc. You may use this API
inside `onIncludeAssets` to modify existing models, and to additionally append
your auto generated models.

To create a `Model` instance that wraps a model:
```java
NamespacedKey modelKey = new NamespacedKey("foo", "item/bar.json");
Path modelPath = resourcePack.getModelPath(modelKey);
JsonResource jsonResource = new JsonResource(resourcePack, modelPath);

Model model = new Model(jsonResource);
```
The Model API always wraps JSON, meaning that changes to the `Model` instance,
even deep changes, will affect the json of the `JsonResource` instance.
Therefore, the changes can be saved by calling `jsonResource.save()`.

There isn't much more to document than what the Javadoc already does, so open
the `Model` class in your IDE (and make sure it has downloaded sources, so you
can see javadoc). You can do things like change display settings, modify
elements, explore Blockbench groups, etc.

## ModelElementList
A `ModelElementList` provides methods for transforming many `Element`s at once.

Like everything regarding the Model API, a `ModelElementList` wraps JSON, and
isn't simply an `ArrayList` of `ModelElement`s. The instance of
`ModelElementList` that `Model#getAllElements` returns is tied to the model's
json, meaning that additions to the `ModelElementList` will result in an
addition to the json as well.

For example, this can be used to add an element:
```java
Model model = new Model(jsonResource);
ModelElement newElement = new ModelElement(
    new Vector(2, 2, 2), // to
    new Vector(4, 4, 4)  // from
);
model.getAllElements().add(newElement);
jsonResource.save();
```

## Another way to save models
You do not need to pass a `JsonResource` to a `Model`. You can simply pass a
`JsonObject` to wrap, or use the no-args constructor to create a new empty
model. You can always get the `JsonObject` being wrapped by calling `getJson`
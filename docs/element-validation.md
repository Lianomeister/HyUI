# Element Validation

This document is the property checklist we validate against for HyUI `.ui` files and builders.

## Syntax Notes

- `$Var = "path/to/file.ui"` imports another UI file.
- `$Var.@TemplateName` references a template from an import.
- `@Name = "Value"` defines a named inline property; it can also be referenced as `$Var.@Name`.

## Restrictions with appendInline

- We cannot reference variables, or imported files easily.
- We can however reference variables within a `UICommandBuilder.set(...)` using `Value.ref(document, variableName)`

## Validation in HyUI

- We do not validate supplied .ui files when using `.fromFile()` (yet).
- We silently fail when validating HYUIML, making sure there are no crashes and exclude invalid properties/styles.

## Common Properties (All Elements)

| Property | Type | Notes |
| --- | --- | --- |
| `#Id` | String | Use `#Id` after the element type to assign an ID. |
| `Anchor` | Anchor | Positioning and sizing definition. |
| `FlexWeight` | Number | Flex layout weight. |
| `Padding` | Padding | Inner spacing definition. |
| `Background` | PatchStyle/String | PatchStyle object or style reference. |
| `Visible` | Boolean | Controls visibility. |
| `TooltipTextSpans` | Message | Tooltip text span data. |
| `HitTestVisible` | Boolean | Controls whether the element can receive input. |

## Elements

### Container

- `LayoutMode`
- `ScrollbarStyle`
- `ClipChildren`
- `#Content` and `#Title` are `Group` elements and follow group rules.
- The common `@Title` must use `@Text = "Value"` and must be first in the properties list.

### PageOverlay

- `LayoutMode`
- `ScrollbarStyle`
- `ClipChildren`

### Group

- `LayoutMode`
- `ScrollbarStyle`
- `ClipChildren`

### ColorPicker

- `Value`
- `Style` (excluding `TextColor`)

### Label

- `Text`
- `Style` (including `Alignment`, `HorizontalAlignment`, `VerticalAlignment`)

### TimerLabel

- `Seconds`
- `Style` (including `Alignment`, `HorizontalAlignment`, `VerticalAlignment`)

### TextButton

- `Text` (string, not `@Text`)
- `Disabled`
- `Style` (excluding `Alignment`, `HorizontalAlignment`, `VerticalAlignment`, `TextColor`)
  - A raw `Button` may use `TextColor`.
- `LayoutMode`
- `Overscroll`

### NumberField

- `Value`
- `Format`
- `MaxDecimalPlaces`

### TextField

- `Value`
- `PlaceholderText`
- `MaxLength`
- `MaxVisibleLines`
- `ReadOnly`
- `Password`
- `PasswordChar`
- `AutoGrow`
- `Style` (excluding `TextColor`)

### AssetImage

- `LayoutMode`
- `AssetPath`

### ItemIcon

- `ItemId` (example: `Tool_Pickaxe_Crude`)

### Sprite

- `TexturePath`
- `Frame` as `(Width: X, Height: Y, PerRow: Z, Count: A)` with all values as integers
- `FramesPerSecond`

### CheckBoxWithLabel

- `@Text` must be first and must be present.

### Template Processor

The Template Processor lets you pre-process HYUIML with variables, filters, and reusable components before HyUI parses it into builders. The showcase command is a good reference: `src/main/java/au/ellie/hyui/commands/HyUIShowcaseCommand.java`.

#### Variable Interpolation

Use `{{$variable}}` in HYUIML and set values in Java:

```java
TemplateProcessor template = new TemplateProcessor()
    .setVariable("playerName", playerRef.getUsername())
    .setVariable("playerLevel", 42);
```

```html
<p>Player: {{$playerName}}</p>
<p>Level: {{$playerLevel}}</p>
```

Defaults and filters are supported:

```html
<p>Missing value: {{$missing|Not Set}}</p>
<p>Uppercase: {{$playerName|upper}}</p>
<p>Number: {{$playerGold|number}}</p>
```

#### Components (Reusable Blocks)

Register a component template and inject parameters when you use it.

```java
TemplateProcessor template = new TemplateProcessor()
    .registerComponent("statCard", """
        <div style="background-color: #2a2a3e; padding: 10; anchor-width: 120; anchor-height: 60;">
            <p style="color: #888888; font-size: 11;">{{$label}}</p>
            <p style="color: #ffffff; font-size: 18; font-weight: bold;">{{$value}}</p>
        </div>
        """);
```

```html
{{@statCard:label=Blocks Placed,value=12.847}}
{{@statCard:label=Creatures Found,value=23}}
```

Notes:
- Component parameters replace `{{$paramName}}` placeholders inside the component template.
- Component templates can include normal `{{$variable}}` placeholders, which are processed after component inclusion.

#### How the Showcase Uses It

In `HyUIShowcaseCommand`, the `TemplateProcessor` is used to:

- Define player stats (variables) like `playerName`, `playerLevel`, and `playerGold`.
- Register reusable blocks such as `statCard` and `featureItem`.
- Render the final HYUIML string with `PageBuilder.fromTemplate(...)`.

This keeps the HYUIML readable while avoiding duplicated markup for repeated UI patterns.

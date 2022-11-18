# pDraw

#### A 2D Graphic Design Language

___

## Index

1. Introduction
2. Group Rundown
3. **pDraw** - Overview
4. **pDraw** - Documentation
5. **pens** -  Overview
6. **pens** - Documentation
7. Compilation and Execution
8. Example Programs

   ---

## 1. Introduction

This is the report for the project of group P1G1 for Compilers 2021/2022. 

The assignment given was to devise, develop and implement a programming language that would allow for the creation and free-range use of pens that could be used to produce 2D drawings.

Furthermore, part of the specifications implied the creation of a secondary, simpler language, which would allow the processing of files whose content would consist of the definition of pens and subsequent loading to the main language.

To achieve the assigned goals we used **ANTLR4** as our parser generator, implemented in **Java** for **pDraw** and in **Python** for **pens** with our target language being **Python**, using the **turtle** library in order to implement the graphical design components of our language.  

---

## 2. Group Rundown

The following table contains basic information about the members of the group and describes their contribution to the project. 

| NMec   | Name              | email                       | Contribution (%) | Detailed contribution [^1]                                                                      |
|:------:|:----------------- |:--------------------------- |:----------------:|:----------------------------------------------------------------------------------------------- |
| 103530 | André Butuc       | andrebutuc@ua.pt            | 19.4%            | pDraw-grammar 50%<br>pDraw-semantic-analysis 60%<br>examples 20%                                            |
| 102442 | Daniel Ferreira   | danielmartinsferreira@ua.pt | 19.4%            | pens-grammar 70%<br> examples 20%<br> report 100%<br> pDraw-grammar 10%                         |
| 103668 | Gonçalo Silva     | goncalo.silva02@ua.pt       | 19.4%            | pDraw-semantic-analysis 40%<br>examples 20%<br>                                                 |
| 103600 | Guilherme Antunes | guilherme.antunes@ua.pt     | 19.4%            | examples 20%<br> primary-code-generation 100%<br> primary-grammar 40%                           |
| 103182 | João Matos        | joaorpm02@ua.pt             | 3%               | testing 100%                                                                                 |
| 103541 | Pedro Rasinhas    | rasinhas@ua.pt              | 19.4%            | examples 20%<br> pens-semantic-analysis 100%<br> pens-code-generation 100% <br>pens-grammar 30% |

> Group Rundown

[^1]: Topics: pDraw-grammar --- pDraw-semantic-analysis --- pDraw-code-generation--- pens-grammar --- pens-semantic-analysis --- pens-code-generation --- examples  --- report --- testing

---

## 3. **pDraw** - Overview

**pDraw** as a programming language finds its purpose as a multifaceted 2D drawing and painting tool with a focus on being familiar to programmers. 

The core of **pDraw** is the concept of pens, which are a primitive type in **pDraw** and are the starting point for any drawing in this language. 

A pen has multiple attributes and is stuctrured as such:

```java
pen = (status, heading, posX, posY, colour, thickness, pattern)
```

> Pen Structure

Pens mimic their real-life counterparts, having a certain spatial position ( `posx`, `posy`), as well as drawing related characteristics, like `colour` , `thickness` and `pattern` . pDraw also supports common data types such as `num` , `string` , `array` and boolean in addtion to drawing specific types `degree` and `action` .
Speaking of actions, pDraw supports the traditional conditional and cyclical statements, as well as user defined functions. More relevant however, are the drawing specific methods like `move to` , `move by` , `rotate` and `change`.
pDraw is a strongly typed language, with variables being declared as such:

```java
type var = value
```

> Variable Declaration

Lastly, pDraw supports sametype operations and is indentation agnostic and uses {} to separate blocks.

---

## 4. **pDraw** - Documentation

---

### Declaration

To create a variable, one must declare it as such:

```java
type var = value
```

> Variable Declaration

Where `type` is one of the following:

```python
'colour' | 'status' | 'pattern' | 'num' | 'string' | 'pen' | 'boolean' | 'degree' | 'action'
```

> Available Variable Types

Additionally, to create a pen, one may alternatively use the following structure:

```pascal
pen penname = ()
```

> Pen Declaration

Where `penname` is the the name of the new `pen` variable. This variable will have all atributes set to default (see Value Properties table below for more information on default values).

`type` describes the type of `value` that can be stored in `var`, which is itself the name of the declared variable.

Furthermore, each type is passible of being converted into an array of that type with the prefix `array-`.

The following table contains a brief description of each value type.

| Type          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `colour`      | Colour, colour name (`'black' \| 'silver' \| 'grey' \| 'white' \| 'maroon' \| 'red' \| 'purple' \| 'fuchsia' \| 'green' \| 'lime' \| 'olive' \| 'yellow' \| 'navy' \| 'blue' \| 'teal' \| 'aqua'`), `#XXXXXX` or `rgb X X X`.                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `status`      | Either `up` (not ready to draw) or `down`(ready to draw).                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `pattern`     | Pen pattern, `solid`, `dotted`or `dashed`.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ```num```     | Numeric value, either an integer or a floating point number.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ```string```  | Sequence of literal characters.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ```pen```     | Used to implement drawings, has several mandatory attributes (order is arbitrary and undeclared values will be set to default):<br>- `status`:  either `up`  (not ready to draw) or `down`(ready to draw) **(default value: up)** <br>- `heading`:  orientation of pen head, degrees **(default value: 0º)**<br>- `posx`: position on x-axis, number **(default value: 0)**<br>- `posy`: position on y-axis, number **(default value: 0)**<br>- `colour`:  colour, colour name, #XXXXXX or rgb X X X **(default value: black)**<br>- `thickness`: pen thickness, number **(default value: 2)**<br>- `pattern`:  pen pattern, solid, dotted or dashed **(default value: solid)**<br> |
| ```boolean``` | A statement that is either `true` or `false`.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ```degree```  | Value of an angle in degrees.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ```action```  | Pen operator, one of the following: <br>- `move to (posx, posy)`, moves a `pen`/`array-pen` to target position `(posx, posy)`<br>- `move by (distance, optional[angle])`, moves a `pen`/`array-pen` by `distance` units, optionally adding `angle` to the pen's `heading` property<br>- `rotate by (angle)`,  adds `angle` to `pen`/`array-pen` 's `heading` property <br>- `change -property = value`, changes the `property` of a `pen`/`array-pen` to a given `value`<br>                                                                                                                                                                                                        |
| `array-`      | List of a given type, can be one of the following:<br>- `array-colour`, list of `colour`<br>- `array-status`, list of `status`<br>- `array-pattern`, list of `pattern`<br>- `array-num`, list of `num`<br>- `array-string`, list of `string`<br>- `array-pen`, list of `pen`. `pen`s in `array-pen` are linked, applying an `action `to `array-pen` applies that `action`to all `pen`therein.<br>- `array-boolean`, list of `boolean`<br>- `array-degree`, list of `degree`<br>- `array-action`, list of `action`<br>- `array-array-`, allows for the creation of a list of any of the above list types. Can occur indefinitely.<br>                                                |

> Value Properties

The following table contains a (non-exhaustive) list of valid examples of declarations, sorted by type.

| Type          | Example                                                                                                                                |
| ------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `colour`      | `colour col = #298474`                                                                                                                 |
| `status`      | `status stat = up`                                                                                                                     |
| `pattern`     | `pattern pat = dotted`                                                                                                                 |
| ```num```     | `num a = 1`                                                                                                                            |
| ```string```  | `string var = "one"`                                                                                                                   |
| ```pen```     | `pen p1 =(status=up, posx=5, posy=5, heading=45º, colour=red, thickness=5, pattern=solid)`                                             |
| ```boolean``` | `boolean t = true`<br>`boolean f = false`<br>`boolean tf = true or false`<br>                                                          |
| ```degree```  | `degree gr = 90º`                                                                                                                      |
| ```array-```  | `array-pen a1 = [p1, p2, p3]`<br>`array-num a1 = [1, 2, 3]`<br>                                                                        |
| ```action```  | `action mt = move to(6,6)`<br>`action mb = move by(10, 45º)`<br>`action rb = rotate by(30º)`<br>`action ch = change status = down`<br> |

> Declaration Examples

---

### Atribution

Post-declaration, a variable's value can be altered through the following command structure:

```java
var = value
```

> Variable Atribution

Where `var` is the variable's name and `value` it's new value. This value must be consistent with the variable's type.

---

### Functions

The following table summarizes the available in-built functions in pDraw:

| Function                   | Definition                                                                                                 |
| -------------------------- | ---------------------------------------------------------------------------------------------------------- |
| ```print(arg)```           | Displays the value of `arg`.                                                                               |
| ```raise/lower pen```      | Changes the `status` property of pen `pen` to `up`or `down`, respectively.                                 |
| ```pen action```           | Applies `action`to pen `pen`. <br>See action description in Value Properties table in Declaration section. |
| ```add ele to arr```       | Adds element `ele` to array `arr`.                                                                         |
| ```remove idx from arr```  | Removes element at index `idx` from array `arr`.                                                           |
| ```length of list```       | Returns the number of elements in array/string `list`.                                                     |
| ```get idx from arr```     | Returns element at index `idx`from array `arr`.                                                            |
| `input optional[num] (input)`| Returns the content read from the terminal prompt `input`. Optional argument `num` to read a `num`.      |
| `import 'file.pens' as f1` | Imports all pens from file, storing them in an array, `f1`.                                                |

> Built-in Function Properties

The following table summarizes the available in-built functions in pDraw:

| Example                                                                                                                                   | Output                                                                                  |
| ----------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| ```print(abc)```                                                                                                                          | `abc`                                                                                   |
| ```pen p1 = ()```<br>```lower pen```<br>`print(p1)`                                                                                       | `(status=down, posx=0, posy=0, heading=0º, colour=black, thickness=2, pattern=solid)`   |
| ```pen p1 = ()```<br>```action ac = status=down```<br>```p1 ac```<br>`print(p1)`                                                          | `(status=down, posx=0, posy=0, heading=0º, colour=black, thickness=2, pattern=solid)`   |
| ```array-num arr = [1,2]```<br>```add 3 to arr```<br>```print(arr)```<br>                                                                 | `[1,2,3]`                                                                               |
| ```array-num arr = [a,b]```<br>```remove 1 from arr```<br>```print(arr)```<br>                                                            | `[a]`                                                                                   |
| ```array-num a1 = [1,2,3,4,5]```<br>`print(length of a1)`                                                                                 | `5`                                                                                     |
| ```array-num a1 = [1,2,a,4,5]```<br>`print(get 2 from a1 )`                                                                               | `a`
| ```num a = input num("Input a number: ")```<br>`Input a number: 2` <br>`print(a)`                                                                               | `2` |
| `pens.txt: (p1,status=down,posx=0,posy=1,heading=2º, colour=black,thickness=1,pattern=solid)`<br>`import 'pens.txt' as f1`<br>`print(f1)` | `[(status=down, posx=0, posy=1, heading=2º, colour=black, thickness=1, pattern=solid)]` |

> Built-in Function Examples

User defined functions are also supported. They can be defined as such:

```pascal
function fname (optional[arg]) do { content optional[(return ret)] } 
```

> User Defined Function Structure 

Where `fname`is the function's name, `arg`are the optional input arguments of `fname`, `content`is the function's content and `ret` its optional return value. 

---

### Operators

The following table enumerates available operators:

| Type               | Operator     | Definition                                                                |
| ------------------ | ------------ | ------------------------------------------------------------------------- |
| Comment            | `###`        | Comments a line.                                                          |
| Comment            | `#* *#`      | Comments a block.                                                         |
| Boolean/Arithmetic | `()`         | Increase precedence within.                                               |
| Boolean            | `==`         | Evaluates if two boolean statements are equal.                            |
| Boolean            | `!`          | Inverts a boolean value.                                                  |
| Boolean            | `and`        | Logical conjunction.                                                      |
| Boolean            | `or`         | Logical disjunction.                                                      |
| Arithmetic         | `-` (prefix) | Negative. Transforms a `num`into its symmetric value.                     |
| Arithmetic         | `^`          | Exponent. Applies to num.                                                 |
| Arithmetic         | `*`          | Multiplication. Applies to `num*num` and `num*string`.                    |
| Arithmetic         | `/`          | Division. Applies to `num/num` and `degree/num`.                          |
| Arithmetic         | `%`          | Remainder of division. Applies to `num%num` and `degree%num`.             |
| Arithmetic         | `+`          | Sum. Applies to same type operations between `num`, `string`and `degree`. |
| Arithmetic         | `-`          | Subtraction. Applies to same type operations between `num`, `degree`.     |

> Operator Properties

Arithmetic operators follow general math precedence and bollean operators follow formal logic precedence.

---

### Blocks

**pDraw **supports the common block formats `if/elif/else` , `for` and `while` loops like so:

```c
if condition then {content} elif condition2 then {content} else {content}
```

```java
for i in range(limit) do {content}
```

```python
while condition do {}
```

> Block Statements

`if/elif/else` statements support any number of `elif `clauses.

The statements above accept any `boolean`as a `condition`, executing their `content `according to its value.

Extraneously to other languages, `for`statements in **pDraw** allow for direct incrementation. In the structure given above, `i`would increment value until it reaches `range(limit)`- 1.

---

## 5. **pens** - Overview

**pens** serves soley to facilitate the implementation of the `import` function in **pDraw**, allowing for a file of pens to loaded directly into the script.

---

## 6. **pens** - Documentation

A file written in this file describes pens that will be used in pDraw. As such, each line of a **pens** file should follow the following format:

```python
(pname,status=val,posx=val,posy=val,heading=val,colour=val,thickness=val,pattern=val)
```

> **pens** Format

Where `pname`is the name of the declared `pen` variable and the following arguments are its attributes. Refer to the Values Attributes table in section 4. **pDraw** - Documentation for valid values for each field.

Note that **pens** only supports the arithmetic operands `+ - * /` to the same effect as in **pDraw**.

All attribute names must be declared, even without value. In such case, a pen with default values in all fields will be created, as such:

```python
(pname,status=,posx=,posy=,heading=,colour=,thickness=,pattern=)
```

> Pen with Default Values in **pens**

---

## 7. Compilation and Execution
First off, run the following commands to compile:
```
antlr4-build -python pens.g4
antlr4-build
```
> Compiling Commands

To execute, run:

```bash
./run.sh
```

> Interactive Execution

to run pDraw in the command line.

To execute a script, run:

```bash
./run.sh filename.pdraw
```

> Script Execution

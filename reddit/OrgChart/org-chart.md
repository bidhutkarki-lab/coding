# Organization Chart: Build, Display, and Query

## Problem

Build an organization chart from manager–direct-report relationships. Then support displaying the chart and answering questions about reporting relationships.

Complete the phases in order. Phases 1–3 form the core problem. Phase 4 is an advanced follow-up. Alternate versions and optional extensions are listed separately.

Keep input parsing, tree construction, and queries separate. Build the organization once and reuse it across queries.

## Input and Shared Rules

### Primary format: list of lists

Each row contains a manager followed by that manager's **direct reports**:

```python
relations = [
    ["A", "B", "C"],
    ["C", "D"],
    ["B", "E"]
]
```

This means:

- A directly manages B and C.
- C directly manages D.
- B directly manages E.

**A row is not a reporting chain.** `["A", "B", "C"]` does not mean A manages B and B manages C.

### Alternate format: comma-separated strings

```python
relations = ["A,B,C", "C,D", "B,E"]
```

Each string represents the same row as the primary format. Split each string on commas before building the chart. Choose one input format before implementation; supporting both is optional.

### Core assumptions

- Input is nonempty and describes one valid management tree.
- Employee names are unique identifiers, are case-sensitive, and are nonempty.
- There is exactly one root: the employee with no manager.
- Every other employee has exactly one direct manager.
- There are no cycles or duplicate reporting edges.
- A manager has at most one input row. Rows may appear in any order.
- Direct reports retain their order within the manager's row.
- Employees appearing only as reports are leaves and must still be included.
- A single-employee organization can be represented by `[["A"]]`.
- Names in the string format contain no commas or surrounding whitespace.
- Queries for unknown employees raise an appropriate error, such as `ValueError` in Python or `IllegalArgumentException` in Java.

Use exactly four dots (`....`) per indentation level for the examples below. The root is at depth 0.

## Phase 1: Build and Display the Organization

Build the organization from the input and return its full indented tree as a string.

### Requirements

1. Find the root from the relationships; do not assume the first row contains it.
2. Include every employee, including leaves.
3. Print each employee once.
4. Print a manager before their reports, completing each report's subtree before moving to the next report.
5. Preserve direct-report order from the input.

### Example

Input:

```python
[["A", "B", "C"], ["C", "D"], ["B", "E"]]
```

Output:

```text
A
....B
........E
....C
........D
```

## Phase 2: Find All Skip-Level Pairs

Return every `(manager, employee)` pair where the employee is **exactly two reporting edges below** the manager.

A direct report does not qualify. An employee three or more levels below does not qualify.

### Example

For the organization in Phase 1:

```python
[("A", "E"), ("A", "D")]
```

A reaches E through B and reaches D through C.

### Requirements

- Include all qualifying pairs across the entire organization.
- Return an empty collection if no pairs exist.
- Pair order does not matter.

## Phase 3: Display an Employee's Lineage and Subtree

Given a target employee, return:

1. The single path from the root to the target.
2. The target's entire subtree beneath that path.

### Requirements

- Include the target exactly once.
- Exclude branches that are neither on the root-to-target path nor inside the target's subtree.
- Preserve each displayed employee's depth in the original organization.
- Preserve direct-report order within the target's subtree.
- For the root, return the full organization.
- For a leaf, return only the root-to-leaf path.

### Example: target B

```text
A
....B
........E
```

### Example: target D

```text
A
....C
........D
```

## Phase 4: Find the Lowest Common Manager

Given two employees, return the identifier of their lowest common ancestor: the deepest employee whose subtree contains both.

For this version, **an employee belongs to their own subtree**. Therefore, an employee may be the answer even if they are one of the queried employees.

### Examples

| First employee | Second employee | Result |
| --- | --- | --- |
| C | E | A |
| B | E | B |
| E | D | A |
| E | E | E |
| A | D | A |

Return the employee identifier, not the employee's depth. If using node objects, return the corresponding node from the built tree.

## Suggested API

The following Python signatures describe the contract; the implementation may use Java or another language.

```python
class OrgChart:
    def __init__(self, relations: list[list[str]]):
        ...

    # Phase 1
    def render_full_chain(self) -> str:
        ...

    # Phase 2
    def all_skip_level_pairs(self) -> list[tuple[str, str]]:
        ...

    # Phase 3
    def render_chain_for(self, target: str) -> str:
        ...

    # Phase 4
    def lowest_common_manager(self, e1: str, e2: str) -> str:
        ...
```

Standalone functions are also acceptable. If they receive raw relationships on every call, account for the repeated construction cost.

## Alternate Versions

These change the contract of a phase. Choose the version before coding.

### Version A: Relationship Check Instead of Skip-Level Enumeration

Replace Phase 2 with a query for two employees:

```python
def is_ancestor(self, ancestor: str, employee: str) -> bool:
    ...
```

Return `True` if `employee` reports up to `ancestor` through one or more reporting edges; otherwise return `False`.

This version uses a **strict ancestor** relationship: an employee is not their own ancestor.

| Query | Result |
| --- | --- |
| `is_ancestor("A", "E")` | `True` |
| `is_ancestor("B", "E")` | `True` |
| `is_ancestor("E", "B")` | `False` |
| `is_ancestor("B", "C")` | `False` |
| `is_ancestor("B", "B")` | `False` |

Optional variation: accept two employees in either order and return which one is the ancestor, or no relationship. Define the return format before implementation.

### Version B: Strict Common Manager

Change Phase 4 so that the answer must be a **strict ancestor of both employees**. Neither queried employee can be the answer.

Examples:

- B and E return A.
- E and E return B.
- A and D have no common strict manager, so return `None` or an empty optional.

Do not mix this definition with the inclusive LCA definition in the core phase.

## Optional Extensions

### Extension 1: Employees Exactly N Levels Below a Manager

```python
def employees_at_level(self, manager: str, levels: int) -> list[str]:
    ...
```

- `levels = 0` returns the manager.
- `levels = 1` returns direct reports.
- `levels = 2` returns skip-level reports.
- Return an empty list if no employees exist at that level.
- Reject negative levels and unknown managers.
- Preserve left-to-right tree order.

Examples:

```python
employees_at_level("A", 0)  # ["A"]
employees_at_level("A", 1)  # ["B", "C"]
employees_at_level("A", 2)  # ["E", "D"]
employees_at_level("B", 2)  # []
```

### Extension 2: Serialize the Organization

Return the organization in the primary input format.

Use these rules for deterministic output:

- Emit one row per employee with direct reports.
- Emit rows in preorder: manager before each report's subtree.
- Preserve direct-report order inside each row.
- Omit leaf rows, except for a single-employee organization, which returns `[["A"]]`.

Example:

```python
[["A", "B", "C"], ["B", "E"], ["C", "D"]]
```

The result must preserve the organization, but does not need to preserve the original ordering of input rows. Optionally support comma-separated strings as an output format.

### Extension 3: Validate Untrusted Input

Remove the valid-tree assumption. Reject invalid input with a clear error.

Validate:

- Empty input, empty rows, or blank identifiers.
- Repeated manager rows or duplicate reporting edges under the chosen input contract.
- An employee reporting to themselves.
- An employee assigned to multiple managers.
- Zero roots or multiple roots.
- Cycles, including a disconnected cycle alongside an otherwise valid rooted component.
- Employees unreachable from the selected root.

**Exactly one root alone does not prove the input is a valid tree.**

### Extension 4: Repeated Queries at Scale

Assume the organization is built once and receives many lineage or common-manager queries.

Discuss:

- Construction cost versus per-query cost.
- The effect of a very deep tree on recursive traversal.
- Whether preprocessing for repeated LCA queries is worthwhile.
- How changing reporting relationships would affect cached data.

Implement extra preprocessing only if requested.

## Edge Cases to Cover

| Case | Expected behavior |
| --- | --- |
| One employee | Full display contains that employee; no skip-level pairs |
| Root appears in a later row | Root is still identified correctly |
| Employee appears only as a report | Employee is included as a leaf |
| One long reporting chain | Indentation and lineage remain correct |
| Manager with many reports | Input report order is preserved |
| Target is the root | Lineage display equals full display |
| Target is a leaf | Lineage display contains only its ancestor path and itself |
| Both LCA inputs are identical | Core version returns that employee |
| Query contains an unknown employee | Raise the agreed error |

## Implementation Guidance — Optional Reading

This section contains hints. Skip it when practicing without guidance.

### Representation

A simple representation is enough for all core phases:

- `children`: employee identifier → ordered list of direct reports.
- `parent`: employee identifier → direct manager.
- `root`: the single employee without a parent.

In Java, these can be `Map<String, List<String>>`, `Map<String, String>`, and `String`. A custom node class is optional; returning an identifier is sufficient when names uniquely identify employees.

Process the relationships to populate the maps, then determine the root from all known employees. Full validation, when enabled, may require additional traversal.

### Query Hints

- Full display: traverse the tree while tracking depth.
- Skip-level pairs: inspect each employee's children's children.
- Lineage display: recover the root-to-target path, then display only the target's descendants. Do not print the target twice.
- Lowest common manager: collect one employee's ancestors, including themselves, then walk upward from the other employee to the first match.

### Complexity Targets

Let `n` be the number of employees, `h` the maximum tree height, `p` the number of employees on the root-to-target path, and `s` the size of the target's subtree, including the target.

| Operation | Time target | Notes |
| --- | --- | --- |
| Build a valid tree | O(n) | Excludes character-level input parsing costs |
| Full display | O(n + L) | L is the number of output characters |
| All skip-level pairs | O(n) | Each employee has at most one grandparent |
| Target lineage and subtree | O(p + s + L) | L is the number of output characters for this query |
| Lowest common manager | O(h) | Ancestor-set approach uses O(h) extra space |
| Stored organization | O(n) space | Children, parents, and employee identifiers |

Indented output can contain O(n²) characters for a chain-shaped organization, even though traversal visits only O(n) employees. Include output size when discussing rendering cost.

For many LCA queries on a static tree, binary lifting is an optional tradeoff: O(n log n) preprocessing and storage, with O(log n) per query.

## Review Focus

- Correct interpretation of rows as direct-report lists.
- Clear separation of parsing, construction, and queries.
- Correct root detection and stable report ordering.
- Correct lineage indentation without duplicating the target.
- Explicit handling of LCA semantics and unknown employees.
- Complexity that accounts for both traversal and generated output.

# Advisor

### Advisor is a software tool designed to make registration for classes dramatically easier.
Using a web platform, users use the select all option for the PAWS shopping cart page and copy the content. It's then pasted into the website where preferences and filters are set. Hit the process button and after a few seconds, the best options are presented.

## Current Status
### Generation
Backend is still in active development. The generation algorithm, while completem is not filtered and is having some issues with courses that give 2 different meeting locations at the same time. This is common for courses that may meet in one room or another based on the week. Currently the system does not detect this as an issue and inserts both events into the schedule instead of one distinct location based on 2 locations. 

### Filtering
Filtering options still need to be determined, but the current plans include:
- Heuristics:
  - Start time heruistic (nothing before)
  - End time heuristic (nothing after)
  - Balance load per day
  - Prefer certain professors
  - Prefer certain sections
  - Avoid certain days of the week
  - "No class" time blocks heuristic
- Hard Filters:
  - Hour limit per day
  - Back-to-back course limit
  - Start time filter (nothing before)
  - End time filter (nothing after)
  - "No class" time blocks filter
  
## UI
UI will be a static site served over nginx using a javascript-powered interactive bootstrap frontend for selecting filterings. The primary goal will be as much *simplicity and intuitiveness* as possible, even if it intrudes on full desired functionality.

## Adapter
Communication between front and back will be via JSON requests. Jobs will have a 10 second generation timeout, more than reasonable from test cases, and requests will queue serving 2 at a time. 

DOS attacks are a serious issue that will need to be addressed.

## Contribution
I'm happy to have people jump in on this project. Front end is not my strong suit and I'd be more than happy to have a hand in putting this together.

## Licencing
GNU Lesser General Public License version 3
  

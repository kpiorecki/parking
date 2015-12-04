Introduction
------------

Parking is Java EE 7 application supporting parking space booking. It is useful in scenario where there are more users than available parking space. Places are assigned to users for given day depending on scheduling algorithm. It takes into account:

- user type (VIP or regular user)
- user points (preferred users with less points - points are added for every assigned day)
- booking timestamp (preferred users who earlier booked the place)

In long-term available places are shared fairly amongst all users.

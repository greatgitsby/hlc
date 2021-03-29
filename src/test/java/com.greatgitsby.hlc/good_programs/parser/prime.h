{
This is a simple program to test it a number is prime
Author: 1696920
}

begin
   variable p;
   variable i;
   
   p := 13;
   i := 2;
   
   { If i divides p, p is not prime }
   if (p / i) = 0 then
   begin
      print p;
      print " is not prime.\n"
   end
   else
   begin
      i := 3;
      while i < p do
      begin
         { If i divides p, p is not prime. Set i to p + 3 to exit loop, else increment i }
         if (p / i) = 0 then
            i := p + 3
         else
            i := i + 2
      end;
      
      print p;
      
      { if i equals p + 3, we exited from finding a divisible term, else p is prime }
      if (p + 3) = i then
         print " is not prime.\n"
      else
         print " is prime.\n"
   end
end

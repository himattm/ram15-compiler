
class Helper():

	def go(self):
		print ((5 * 4) + 3)
		print (3 - (2 * 6))
		print self.stop(5)
		if (4 < 5):
			print 1
			if (5 < 6):
				print 2
			else:
				print 21


		else:
			print 11


		return 5
	def stop(self, a):
		print a
		return True
if __name__ == '__main__':
	print Helper().go()


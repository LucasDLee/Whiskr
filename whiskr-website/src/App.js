import { Hero } from './components/Hero';
import { Features } from './components/Features'
import { Footer } from './components/Footer';
import './App.css';
import { About } from './components/About';
import { Team } from './components/Team';
import { Separator } from './components/Separator';

function App() {
  return (
    <>
      <Hero />
      <About />
      <Separator />
      <Features />
      <Separator />
      <Team />
      <Footer />
    </>
  );
}

export default App;

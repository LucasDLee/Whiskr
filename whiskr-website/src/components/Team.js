import { TeamCard } from "./TeamCard"
import ciraImg from "../images/cira.webp"
import deniseImg from "../images/denise.jpg"
import lucasImg from "../images/lucas.jpg"
import victorImg from "../images/victor.jpg"

export const Team = () => {

  const team = [
    {
      description: ["Hello! I'm Cira, a 3rd-year student at SFU majoring in Computing Science and minoring in Math.",
                    "Outside of school, I'm passionate about arts and food—I especially enjoy crafting, drawing, sewing, and cooking.",
                    "As part of the Whiskr team, I collaborated with my group member Lucas to develop the chat feature that allows users to interact with CatBot. My contributions focused on integrating Firebase to securely store and manage chat histories, while also designing an intuitive interface to ensure seamless user interactions. Additionally, I implemented the random cat facts feature to make Whiskr more entertaining and engaging.",
                    "Thank you for checking out Whiskr! I hope our app brings you and all cat lovers around the world a delightful experience."], // small description about what the team member did
      image: ciraImg,
      name: "Cira Chen" // team member name
    },
    {
      description: ["Hi! I'm Denise, a 4th year computing science student at SFU.",
                    "Outside of academics, I'm probably watching my favourite show, Bones, playing volleyball, or jamming out to the newest hit music!",
                    "The developmental journey of Whiskr has been nothing short of challenging, but our team has designed a product that will be useful to current and prospective cat owners around the globe.",
                    "My responsibilities lie in the implementation of the CatMap - to connect people to pet services local to them - and the direction of our pitch and progress updates to ensure a consistent delivery. Thank you for using Whiskr!"], // small description about what the team member did
      image: deniseImg,
      name: "Denise Wong" // team member name
    },
    {
      description: ["Hello there! I'm Lucas, a 4th year CS undergraduate at Simon Fraser University",
                    "Outside of school, I'm passionate about staying active, whether it's cycling through scenic routes or hitting the gym for a solid workout. When I'm not exercising, you'll find me diving into the latest strategy games, always up for a challenge and sharpening my mind.",
                    "My responsibilty in this project was to build and train a custom chatbot with cat-related knowledge. I utilized Botpress to build the bot and implemented a REST API to recieve requests from the bot to integrate it with our app.",
                    "Thanks for taking a look at Whiskr! We hope to add many more features in the future."], // small description about what the team member did
      image: lucasImg,
      name: "Lucas Lee" // team member name
    },
    {
      description: ["Hi there! My name is Victor, and I'm currently a student at the Simon Fraser University, where I'm majoring in Computer Science.",
                    "When I'm not hitting the books, you can often find me on the gym court, as I have a deep love for Volleyball. This sport not only keeps me active and healthy but also teaches me valuable life skills like teamwork, perseverance, and strategic thinking.",
                    "In my role as a developer on the Whiskr project, I am responsible  for implementing the adoption listing feature using the API from RescueGroups.org.",
                    "I'm excited about the future and eager to continue growing both personally and professionally. Thank you for taking the time to learn a little bit about me!"], // small description about what the team member did
      image: victorImg,
      name: "Victor Cho" // team member name
    }
  ]

  return (
    <section className="featuresSection">
      <div className="featureSeparator">
        <div className="featureHeader">Team</div>
        <div className="featureSubheader">An Outstanding 4-Person Group</div>
        <div className="featureDescription">Our innovative team developed this Android application from the ground-up.</div>
      </div>
      <div className="teamGrid">
        {team.map((member, val) => {
          return <TeamCard
                  key={val}
                  description={member.description}
                  image={member.image}
                  imageDescription={member.imageDescription}
                  name={member.name} />
        })}
      </div>
    </section>
  )
}
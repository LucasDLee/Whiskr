import { TeamCard } from "./TeamCard"
import githubIcon from "../images/github.webp"

export const Team = () => {

  const team = [
    {
      description: "DESCRIPTION", // small description about what the team member did
      image: githubIcon,
      jobs: ["a", "b", "c"], // tasks the team member did
      name: "PERSON" // team member name
    },
    {
      description: "DESCRIPTION", // small description about what the team member did
      image: githubIcon,
      jobs: ["a", "b", "c"], // tasks the team member did
      name: "PERSON" // team member name
    },
    {
      description: "DESCRIPTION", // small description about what the team member did
      image: githubIcon,
      jobs: ["a", "b", "c"], // tasks the team member did
      name: "PERSON" // team member name
    },
    {
      description: "DESCRIPTION", // small description about what the team member did
      image: githubIcon,
      jobs: ["a", "b", "c"], // tasks the team member did
      name: "PERSON" // team member name
    }
  ]

  return (
    <section className="featuresSection">
      <div className="featureSeparator">
        <div className="featureHeader">Team</div>
        <div className="featureSubheader">An Outstanding 4-Person Group</div>
        <div className="featureDescription">Our innovative 4-person team developed our application from the ground-up.</div>
      </div>
      <div className="teamGrid">
        {team.map((member, val) => {
          return <TeamCard
                  key={val}
                  description={member.description}
                  image={member.image}
                  imageDescription={member.imageDescription}
                  jobs={member.jobs}
                  name={member.name} />
        })}
      </div>
    </section>
  )
}
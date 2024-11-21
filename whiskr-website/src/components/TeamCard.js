export const TeamCard = (props) => {
  return (
    <div className="teamCard">
      <img src={props.image} alt={props.imageDescription} />
      <div className="teamCardContents">
        <div className="teamCardName">{ props.name }</div>
        <div className="teamCardDescription">{ props.description }</div>
        <ul>
          {props.jobs.map((activity, val) => {
            return <li key={val}>{activity}</li>
          })}
        </ul>
      </div>
    </div>
  )
}